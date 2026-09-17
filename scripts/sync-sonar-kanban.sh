#!/usr/bin/env bash
# Sincroniza issues abertas no SonarQube/SonarCloud com um GitHub Project (kanban).
#
# O que faz:
#   1. Para cada issue aberta no Sonar sem uma GitHub Issue correspondente -> cria a issue,
#      adiciona no Project na coluna "Pendente" e tenta atribuir responsável via git blame.
#   2. Para issues do GitHub geradas pelo Sonar cuja "sonar-key" não está mais entre as
#      issues abertas do Sonar (ou seja, foi resolvida) -> fecha a issue e move para "Concluído".
#
# OBS IMPORTANTE: esta versão evita por completo os subcomandos "gh project item-add"
# e "gh project item-list" com a flag --owner, pois a versão do gh CLI usada no runner
# tem um bug conhecido ("unknown owner type") na resolução interna dessa flag para
# esses subcomandos específicos. Em vez disso, usamos "gh api graphql" diretamente
# (a mesma API por trás dos comandos, só que sem passar pela lógica com bug).
# "gh project item-edit" não usa --owner (recebe --project-id diretamente), então
# esse comando continua sendo usado normalmente.
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
PROJECT_ID=$(gh api graphql -f query='
  query($login: String!, $number: Int!) {
    user(login: $login) {
      projectV2(number: $number) { id }
    }
  }' -f login="$OWNER" -F number="$PROJECT_NUMBER" --jq '.data.user.projectV2.id')

if [ -z "$PROJECT_ID" ] || [ "$PROJECT_ID" == "null" ]; then
  echo "ERRO: nao foi possivel resolver o PROJECT_ID. Verifique OWNER/PROJECT_NUMBER."
  exit 1
fi
echo "    PROJECT_ID=$PROJECT_ID"

echo "==> Garantindo que a label '$LABEL' existe..."
gh label create "$LABEL" --repo "$OWNER/$REPO" --color "d93f0b" \
  --description "Issue gerada automaticamente a partir de um achado do SonarQube" 2>/dev/null || true

echo "==> Baixando issues do GitHub já geradas pelo Sonar (abertas e fechadas)..."
gh issue list --repo "$OWNER/$REPO" --label "$LABEL" --state all --limit 500 \
  --json number,body,state >gh-issues.json

echo "==> Baixando itens atuais do Project (via GraphQL, com paginação)..."
PROJECT_ITEMS_FILE="project-items.json"
echo "[]" >"$PROJECT_ITEMS_FILE"
CURSOR="null"
while true; do
  PAGE=$(gh api graphql -f query='
    query($project: ID!, $after: String) {
      node(id: $project) {
        ... on ProjectV2 {
          items(first: 100, after: $after) {
            pageInfo { hasNextPage endCursor }
            nodes {
              id
              content {
                ... on Issue { number }
              }
            }
          }
        }
      }
    }' -f project="$PROJECT_ID" -F after="$CURSOR")

  jq -s '.[0] + [.[1].data.node.items.nodes[]]' "$PROJECT_ITEMS_FILE" <(echo "$PAGE") >tmp.json
  mv tmp.json "$PROJECT_ITEMS_FILE"

  HAS_NEXT=$(jq -r '.data.node.items.pageInfo.hasNextPage' <<<"$PAGE")
  CURSOR=$(jq -r '.data.node.items.pageInfo.endCursor' <<<"$PAGE")
  [ "$HAS_NEXT" == "true" ] || break
done

SONAR_KEYS_OPEN=$(jq -r '.issues[].key' "$SONAR_ISSUES_FILE")

# Processa uma issue do Sonar. Roda em subshell com "set -e" proprio: se algo
# falhar aqui dentro, so essa issue e' pulada (com aviso), o script principal
# continua para as demais - em vez de o "set -e" do topo matar tudo de uma vez.
process_new_issue() {
  (
    set -e
    local issue="$1"
    local KEY MESSAGE RULE SEVERITY COMPONENT LINE
    local BODY ISSUE_URL ISSUE_NUMBER CONTENT_ID ITEM_ID

    KEY=$(jq -r '.key' <<<"$issue")
    MESSAGE=$(jq -r '.message' <<<"$issue")
    RULE=$(jq -r '.rule' <<<"$issue")
    SEVERITY=$(jq -r '.severity' <<<"$issue")
    COMPONENT=$(jq -r '.component' <<<"$issue" | sed 's/^[^:]*://')
    LINE=$(jq -r '.line // "N/A"' <<<"$issue")

    echo "  -> Nova issue do Sonar: $KEY ($COMPONENT:$LINE)"

    BODY=$(
      cat <<EOF
**Regra:** $RULE
**Severidade:** $SEVERITY
**Arquivo:** $COMPONENT:$LINE

$MESSAGE

<!-- sonar-key: $KEY -->
EOF
    )

    ISSUE_URL=$(gh issue create --repo "$OWNER/$REPO" --title "[Sonar] $MESSAGE" \
      --body "$BODY" --label "$LABEL")

    ISSUE_NUMBER=$(basename "$ISSUE_URL")

    CONTENT_ID=$(gh api graphql -f query='
    query($owner: String!, $repo: String!, $number: Int!) {
      repository(owner: $owner, name: $repo) {
        issue(number: $number) { id }
      }
    }' -f owner="$OWNER" -f repo="$REPO" -F number="$ISSUE_NUMBER" --jq '.data.repository.issue.id')

    ITEM_ID=$(gh api graphql -f query='
    mutation($project: ID!, $content: ID!) {
      addProjectV2ItemById(input: {projectId: $project, contentId: $content}) {
        item { id }
      }
    }' -f project="$PROJECT_ID" -f content="$CONTENT_ID" --jq '.data.addProjectV2ItemById.item.id')

    gh project item-edit --id "$ITEM_ID" --project-id "$PROJECT_ID" \
      --field-id "$FIELD_STATUS_ID" --single-select-option-id "$OPTION_PENDENTE"

    echo "     criada: $ISSUE_URL "
  )
}

echo ""
echo "==> Processando issues abertas no Sonar..."
jq -c '.issues[]' "$SONAR_ISSUES_FILE" | while read -r issue; do
  KEY=$(jq -r '.key' <<<"$issue")
  EXISTING=$(jq -r --arg key "$KEY" '.[] | select(.body | contains($key)) | .number' gh-issues.json | head -1)

  if [ -n "$EXISTING" ]; then
    continue
  fi

  if ! process_new_issue "$issue"; then
    echo "     ERRO ao processar $KEY - pulando para a proxima (nao interrompe o restante)"
  fi
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

    ITEM_ID=$(jq -r --arg n "$NUMBER" '.[] | select(.content.number == ($n|tonumber)) | .id' "$PROJECT_ITEMS_FILE" | head -1)

    if [ -n "$ITEM_ID" ]; then
      gh project item-edit --id "$ITEM_ID" --project-id "$PROJECT_ID" \
        --field-id "$FIELD_STATUS_ID" --single-select-option-id "$OPTION_CONCLUIDO"
    fi
  fi
done

echo ""
echo "Sincronização concluída."
