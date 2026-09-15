#!/usr/bin/env bash
# Sincroniza issues abertas no SonarQube/SonarCloud com um GitHub Project (kanban).
#
# O que faz:
#   1. Para cada issue aberta no Sonar sem uma GitHub Issue correspondente -> cria a issue,
#      adiciona no Project na coluna "Pendente" e tenta atribuir responsável via git blame.
#   2. Para issues do GitHub geradas pelo Sonar cuja "sonar-key" não está mais entre as
#      issues abertas do Sonar (ou seja, foi resolvida) -> fecha a issue e move para "Concluído".
#
# Requisitos: gh (autenticado via $GH_TOKEN), jq, curl, git.
# Variáveis de ambiente esperadas: GH_TOKEN

set -euo pipefail

# ---------- CONFIGURAÇÃO (ajuste aqui) ----------
OWNER="pabkaua"
REPO="Sistema-alugueis"
PROJECT_NUMBER=2
FIELD_STATUS_ID="PVTSSF_lAHODC8I4c4BjkdCzhiYTFI"
OPTION_PENDENTE="61e4505c"
OPTION_CONCLUIDO="98236657"
LABEL="sonar-issue"
SONAR_ISSUES_FILE="sonar-issues.json"
# --------------------------------------------------

echo "==> Resolvendo o ID interno do Project #$PROJECT_NUMBER..."
PROJECT_ID=$(gh project view "$PROJECT_NUMBER" --owner "$OWNER" --format json --jq '.id')
echo "    PROJECT_ID=$PROJECT_ID"

echo "==> Garantindo que a label '$LABEL' existe..."
gh label create "$LABEL" --repo "$OWNER/$REPO" --color "d93f0b" \
  --description "Issue gerada automaticamente a partir de um achado do SonarQube" 2>/dev/null || true

echo "==> Baixando issues do GitHub já geradas pelo Sonar (abertas e fechadas)..."
gh issue list --repo "$OWNER/$REPO" --label "$LABEL" --state all --limit 500 \
  --json number,body,state > gh-issues.json

SONAR_KEYS_OPEN=$(jq -r '.issues[].key' "$SONAR_ISSUES_FILE")

echo ""
echo "==> Processando issues abertas no Sonar..."
jq -c '.issues[]' "$SONAR_ISSUES_FILE" | while read -r issue; do
  KEY=$(jq -r '.key' <<<"$issue")
  MESSAGE=$(jq -r '.message' <<<"$issue")
  RULE=$(jq -r '.rule' <<<"$issue")
  SEVERITY=$(jq -r '.severity' <<<"$issue")
  COMPONENT=$(jq -r '.component' <<<"$issue" | sed 's/^[^:]*://')
  LINE=$(jq -r '.line // "N/A"' <<<"$issue")

  EXISTING=$(jq -r --arg key "$KEY" '.[] | select(.body | contains($key)) | .number' gh-issues.json | head -1)

  if [ -n "$EXISTING" ]; then
    continue
  fi

  echo "  -> Nova issue do Sonar: $KEY ($COMPONENT:$LINE)"

  # Tenta descobrir o responsável via git blame na linha do problema
  ASSIGNEE=""
  if [ "$LINE" != "N/A" ] && [ -f "$COMPONENT" ]; then
    COMMIT=$(git blame -L "${LINE},${LINE}" --porcelain -- "$COMPONENT" 2>/dev/null | head -1 | cut -d' ' -f1 || true)
    if [ -n "${COMMIT:-}" ]; then
      ASSIGNEE=$(gh api "repos/$OWNER/$REPO/commits/$COMMIT" --jq '.author.login // empty' 2>/dev/null || true)
    fi
  fi

  BODY=$(cat <<EOF
**Regra:** $RULE
**Severidade:** $SEVERITY
**Arquivo:** $COMPONENT:$LINE

$MESSAGE

<!-- sonar-key: $KEY -->
EOF
)

  if [ -n "$ASSIGNEE" ]; then
    ISSUE_URL=$(gh issue create --repo "$OWNER/$REPO" --title "[Sonar] $MESSAGE" \
      --body "$BODY" --label "$LABEL" --assignee "$ASSIGNEE")
  else
    ISSUE_URL=$(gh issue create --repo "$OWNER/$REPO" --title "[Sonar] $MESSAGE" \
      --body "$BODY" --label "$LABEL")
  fi

  ITEM_ID=$(gh project item-add "$PROJECT_NUMBER" --owner "$OWNER" --url "$ISSUE_URL" --format json --jq '.id')

  gh project item-edit --id "$ITEM_ID" --project-id "$PROJECT_ID" \
    --field-id "$FIELD_STATUS_ID" --single-select-option-id "$OPTION_PENDENTE"

  echo "     criada: $ISSUE_URL (responsável: ${ASSIGNEE:-nenhum encontrado})"
done

echo ""
echo "==> Verificando issues do GitHub cujo problema já foi resolvido no Sonar..."
jq -r '.[] | select(.state=="OPEN") | @base64' gh-issues.json | while read -r row; do
  _jq() { echo "$row" | base64 --decode | jq -r "$1"; }
  NUMBER=$(_jq '.number')
  BODY=$(_jq '.body')
  KEY=$(grep -oP '(?<=sonar-key: )[^ ]+(?= -->)' <<<"$BODY" || true)

  if [ -n "$KEY" ] && ! grep -qx "$KEY" <<<"$SONAR_KEYS_OPEN"; then
    echo "  -> Resolvida: fechando issue #$NUMBER (sonar-key $KEY)"
    gh issue close "$NUMBER" --repo "$OWNER/$REPO" --comment "Resolvido no SonarQube ✅ (sincronização automática)"

    ITEM_ID=$(gh project item-list "$PROJECT_NUMBER" --owner "$OWNER" --format json \
      --jq --arg n "$NUMBER" '.items[] | select(.content.number == ($n|tonumber)) | .id')

    if [ -n "$ITEM_ID" ]; then
      gh project item-edit --id "$ITEM_ID" --project-id "$PROJECT_ID" \
        --field-id "$FIELD_STATUS_ID" --single-select-option-id "$OPTION_CONCLUIDO"
    fi
  fi
done

echo ""
echo "Sincronização concluída."
