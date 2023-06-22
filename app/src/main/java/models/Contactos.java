package models;

public class Contactos {
    private Integer id;
    private String nombres;
    private String telefonos;
    private String notas ;
    private byte[] imagen ;

    public Contactos(Integer id, String nombres, String telefonos, String notas, byte[] imagen) {
        this.id = id;
        this.nombres = nombres;
        this.telefonos = telefonos;
        this.notas = notas;
        this.imagen = imagen;
    }

    public Contactos() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[]  imagen) {
        this.imagen = imagen;
    }
}
