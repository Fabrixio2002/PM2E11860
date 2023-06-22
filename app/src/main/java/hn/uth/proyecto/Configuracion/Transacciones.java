package hn.uth.proyecto.Configuracion;

public class Transacciones
{
   // Nombre de la base de datos
    public static final String NameDatabase = "Proyecto";

    // tABLAS DE bASE DE DATOS

    public static final  String tablaContactos = "contactos";

    // Campos de la tabla personas
    public static final String id = "id";
    public static final String nombres = "nombres";
    public static final String telefonos = "telefonos";
    public static final String notas = "notas";
    public static final String pais = "pais";
    public static final String imagen = "imagen";

    // DDL Create and Drop
    public static final String CreateTableContactos = "CREATE TABLE CONTACTOS" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, nombres TEXT, telefonos TEXT, notas TEXT, " +
            "pais TEXT, imagen BLOB)";


 public static final String DROPTableContactos = "DROP TABLE IF EXISTS contactos";

    // DML
    public static final String SelectTableContac = "SELECT * FROM " + Transacciones.tablaContactos;

}
