import com.loja.model.Fornecedor;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class NvForneAdminController {

    @FXML
    private TextField CadastroCnpj;

    @FXML
    private TextField CadastroID;

    @FXML
    private TextField CadastroNome;

    @FXML
    private TextField CadastroTel;

    @FXML
    private Button botaoCancelar;

    @FXML
    private Button botaoSalvarForne;

    private ILojaFacade facade;
    private Fornecedor fornecedor;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
        if (fornecedor == null) return;

        CadastroID.setText(fornecedor.getId());
        CadastroID.setDisable(true);
        CadastroNome.setText(fornecedor.getNome());
        CadastroCnpj.setText(fornecedor.getCnpj());
        CadastroTel.setText(fornecedor.getTelefone());
    }


    @FXML
    void buscar(ActionEvent event) {

    }

}
