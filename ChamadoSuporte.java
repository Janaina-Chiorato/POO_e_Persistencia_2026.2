public class ChamadoSuporte{
    // Criando Atributos 
    private int id;
    private String descrição;
    private boolean aberto;
    private AtivoTI ativoRealacionado;

    // Construtor
    public ChamadoSuporte(int id, String descrição, AtivoTI ativoRelacionado){
        this.id = id;
        this.descrição = descrição;
        this.aberto = true;
        this.ativoRelacionado = ativoRelacionado;
    }

    public void fecharChamado(){
        this.aberto = false;

    }

    public AtivoTI getAtivoRelacionado(){
        return this.ativoRealacionado; // Vai retornar o endereço da varivel
    }

}