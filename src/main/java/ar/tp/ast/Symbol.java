package ar.tp.ast;

public class Symbol {
    public String tipo;
    public String uso;

    public Symbol(String tipo,String uso){
        this.tipo=tipo;
        this.uso=uso;
    }

    public String toString(){
        return this.tipo+" "+this.uso;
    }
}
