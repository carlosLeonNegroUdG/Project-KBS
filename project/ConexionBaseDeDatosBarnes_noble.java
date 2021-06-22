package examples.kbb.project;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class ConexionBaseDeDatosBarnes_noble{
    private static Connection conexion;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "";
    private static final String url = "jdbc:mysql://localhost:3306/barnes_noble";
    public ConexionBaseDeDatosBarnes_noble(){
        conexion=null;
        try {
            Class.forName(driver);
            conexion=DriverManager.getConnection(url,user,password);
            
            
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error la conectar ");
        }
    }

    public boolean realizarConsulta(String query){
        ConexionBaseDeDatosBarnes_noble cn=new ConexionBaseDeDatosBarnes_noble();
        ResultSet rs;
        Statement st;
        Boolean productoEncontrado=false;
        try{
            
            st = cn.conexion.createStatement();
            rs=st.executeQuery(query);
            //System.out.println(query);
            if(rs!=null){
                if(rs.next()){
                    productoEncontrado=true;
                    System.out.println("Se encontro el producto con el agente vendedor Barnes & Noble");
                }
            }
            else{
                productoEncontrado=false;
            }
                //System.out.println("Se ha encontrado el libro con el siguiente nombre");
                
           
        }
        catch(Exception e){
            System.out.println("No se pudo realizar la consulta");
        }
        return productoEncontrado;
    }

    public Boolean actualizarCatalogo(String query){
        ConexionBaseDeDatosBarnes_noble cn=new ConexionBaseDeDatosBarnes_noble();
        ResultSet rs;
        Statement st;
        PreparedStatement stActualizar;
        String queryActualizar,nombre,categoria;
        int codigoCasteado,cantidadCasteada,rsActualizar,reducirExistencia;
        String codigo,cantidad,precio;
        double precioCasteado;
        Boolean productoEncontrado=false;
        Boolean productoActualizado=false;
        try{
            
            st = cn.conexion.createStatement();
            rs=st.executeQuery(query);
            
            if(rs!=null){
                    if(rs.next()){
                        productoEncontrado=true;
                        codigo=rs.getObject(1).toString();
                        nombre=rs.getObject(2).toString();
                        categoria=rs.getObject(3).toString();
                        precio=rs.getObject(4).toString();
                        cantidad= rs.getObject(5).toString();
                        codigoCasteado=Integer.parseInt(codigo);
                        precioCasteado=Double.parseDouble(precio);
                        cantidadCasteada=Integer.parseInt(cantidad);
                        reducirExistencia=cantidadCasteada-1;

                        queryActualizar="UPDATE product SET quantity = " +reducirExistencia+ " WHERE name='" +nombre+ "'";
                        stActualizar= cn.conexion.prepareStatement(queryActualizar);
                        rsActualizar=stActualizar.executeUpdate();
                        if(rsActualizar != 0){
                            productoActualizado=true;
                        }
                        else{
                            System.out.println("No se pudo actualizar");
                        }
                    }
                    else{
                        System.out.println("No se encontro el producto con ese nombre");
                    }
                
            }
            else{
                System.out.println("No se ejecuto la consulta");
            }
           
        }
        catch(Exception e){
            System.out.println("No se pudo realizar la consulta");
        }
        return productoEncontrado;
    }

    public Connection getConnection(){
        return conexion;
    }

    public void desconectar(){
        conexion=null;
        if(conexion==null){
            System.out.println("Conexion terminada");
        }
    }
}