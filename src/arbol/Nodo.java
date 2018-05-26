/*
 * Ejemplo desarrollado por Erick Navarro
 * Blog: e-navarro.blogspot.com
 * Septiembre - 2015
 */

package arbol;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Clase que representa un nodo del árbol, es un árbol de expresiones aritméticas por
 * lo que es un árbol binario (cada nodo tiene como máximo 2 hijos).
 * @author Erick Navarro
 */
public class Nodo {
    
    /**
     * Enumeración de los posibles tipos para un nodo del árbol de sintaxis 
     * abstracta.
     */
    public static enum Tipo{
        SUMA,
        RESTA,
        MULTIPLICACION,
        DIVISION,
        NEGATIVO,
        NUMERO
    }
    /**
     * Tipo del nodo.
     */
    private final Tipo tipo;
    /**
     * Hijo izuierdo del nodo o único hijo cuando se trate de la operación
     * unaria NEGATIVO.
     */
    private Nodo operadorIzq;
    /**
     * Hijo derecho del nodo.
     */
    private Nodo operadorDer;
    /**
     * Valor que almacena el nodo cuando se trata de un numero.
     */
    private Double valor;
    /**
     * Variable privada con la que lleva el control de un correlativo  que se le 
     * asignará a cada nodo que es creado, este será único para cada nodo y 
     * servirá para hacer la gráfica de la expresión aritmética con graphviz.
     */
    private static int correlativo=1;
    /**
     * Constante privada que posee cada nodo y es única, funciona como 
     * identificador y será útil para hacer la gráfica de la expresión 
     * aritmética con graphviz.
     */
    private final int id;    
    /**
     * Variable estática que se utiliza para llevar el control de los gráficos
     * que se van generando, es un número de correlativo que se asigna al nombre
     * de los archivos generados.
     */
    private static int numGrafico=0;
    /**
     * Constructor para los nodos que tienen dos hijos, es decir, los de tipo 
     * SUMA, RESTA, MULTIPLICACIÓN Y DIVISION
     * @param operadorIzq operador izquierdo de la operación
     * @param operadorDer operador derecho de la operación
     * @param tipo tipo de operación
     */    
    public Nodo(Nodo operadorIzq, Nodo operadorDer, Tipo tipo) {
        this.tipo = tipo;
        this.operadorIzq = operadorIzq;
        this.operadorDer = operadorDer;
        id=correlativo++;
    }
    /**
     * Constructor para los nodos que solo tienen un hijo, es decir, los de tipo 
     * NEGATIVO
     * @param operadorIzq operador izquierdo de la operación
     * @param tipo tipo de operación
     */
    public Nodo(Nodo operadorIzq, Tipo tipo) {
        this.tipo = tipo;
        this.operadorIzq = operadorIzq;
        id=correlativo++;
    }
    /**
     * Constructor para los nodos que no tienen hijos, es decir, los de tipo 
     * NUMERO
     * @param valor Valor específico del número que almacena el nodo
     */
    public Nodo(Double valor) {
        this.tipo = Tipo.NUMERO;
        this.valor = valor;
        id=correlativo++;
    }
    /**
     * Método que retorna el valor de una expresión aritmética a partir del 
     * árbol de dicha expresión
     * @return Retorna el resultado de la operación.
     */
    public Double getValor(){
        if(tipo== Tipo.DIVISION){
            return operadorIzq.getValor() / operadorDer.getValor();
        }else if(tipo== Tipo.MULTIPLICACION){
            return operadorIzq.getValor() * operadorDer.getValor();
        }else if(tipo== Tipo.RESTA){
            return operadorIzq.getValor() - operadorDer.getValor();
        }else if(tipo== Tipo.SUMA){
            return operadorIzq.getValor() + operadorDer.getValor();
        }else if(tipo== Tipo.NEGATIVO){
            return operadorIzq.getValor() * -1;
        }else{
            //si se trata de un número.
            return valor;
        }
    }
    /**
     * Método que genera el gráfico de la expresión aritmética con graphviz,
     * considerando como la raíz de dicho árbol el actual Nodo. 
     * @return El nombre del archivo en el que se guardó la imagen generada.
     */
    public String graficar() {
        FileWriter fichero = null;
        PrintWriter escritor;
        try
        {
            fichero = new FileWriter("graf"+(++numGrafico)+".dot");
            escritor = new PrintWriter(fichero);
            escritor.print(getCodigoGraphviz());
        } 
        catch (Exception e){
            System.err.println("Error al escribir el archivo graf"+numGrafico+".dot");
        }finally{
           try {
                if (null != fichero)
                    fichero.close();
           }catch (Exception e2){
               System.err.println("Error al cerrar el archivo graf"+numGrafico+".dot");
           } 
        }                        
        try{
          Runtime rt = Runtime.getRuntime();
          rt.exec( "dot -Tjpg -o graf"+numGrafico+".jpg graf"+numGrafico+".dot");
        } catch (Exception ex) {
            System.err.println("Error al generar la imagen para el archivo "
                    + "graf"+numGrafico+".dot");
        }            
        return "graf"+numGrafico+".jpg";
    }
    /**
     * Método que retornoa el código que grapviz usará para generar la imagen 
     * de la expresión aritmética evaluada.
     * @return 
     */
    private String getCodigoGraphviz() {
        return "digraph grafica{\n" +
               "rankdir=TB;\n" +
               "node [shape = record, style=filled, fillcolor=seashell2];\n"+
                getCodigoInterno()+
                "}\n";
    }
    /**
     * Genera el código interior de graphviz, este método tiene la particularidad 
     * de ser recursivo, esto porque recorrer un árbol de forma recursiva es bastante 
     * sencillo y reduce el código considerablemente. 
     * @return 
     */
    private String getCodigoInterno() {
        String etiqueta;
        if(tipo== Tipo.DIVISION){
            etiqueta="nodo"+id+" [ label =\"<C0>|/|<C1>\"];\n";
        }else if(tipo== Tipo.MULTIPLICACION){
            etiqueta="nodo"+id+" [ label =\"<C0>|*|<C1>\"];\n";
        }else if(tipo== Tipo.RESTA){
            etiqueta="nodo"+id+" [ label =\"<C0>|-|<C1>\"];\n";
        }else if(tipo== Tipo.SUMA){
            etiqueta="nodo"+id+" [ label =\"<C0>|+|<C1>\"];\n";
        }else if(tipo== Tipo.NEGATIVO){
            return "nodo"+id+" [ label =\"-|<C1>\"];\n"+
                    operadorIzq.getCodigoInterno() +
                   "nodo"+id+":C1->nodo"+operadorIzq.id+"\n";
        }else{
            //si se trata de un número.
            return "nodo"+id+" [ label =\""+valor+"\"];\n";
        }
        return etiqueta +                     
               operadorIzq.getCodigoInterno() +
               "nodo"+id+":C0->nodo"+operadorIzq.id+"\n" +
               operadorDer.getCodigoInterno() +
               "nodo"+id+":C1->nodo"+operadorDer.id+"\n";
    }    
}
