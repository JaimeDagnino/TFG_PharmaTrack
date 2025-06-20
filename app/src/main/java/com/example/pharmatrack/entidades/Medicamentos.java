package com.example.pharmatrack.entidades;

public class Medicamentos {

    private int id;
    private String nombre;
    private String dosis;
    private int vecesPorDia;
    private String horas;
    private int icono;

    private int diasDeToma;
    private int capsulasRestantes;
    private int dosisPorToma;
    private int comprimidosPorCaja;
    private int umbralAviso;
    private int dosisTomadasHoy;
    private String fechaInicio;

    // Getters y setters...

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public int getVecesPorDia() { return vecesPorDia; }
    public void setVecesPorDia(int vecesPorDia) { this.vecesPorDia = vecesPorDia; }

    public String getHoras() { return horas; }
    public void setHoras(String horas) { this.horas = horas; }
    public int getDiasDeToma() { return diasDeToma; }
    public void setDiasDeToma(int diasDeToma) { this.diasDeToma = diasDeToma; }


    public int getIcono() { return icono; }
    public void setIcono(int icono) { this.icono = icono; }

    public int getCapsulasRestantes() { return capsulasRestantes; }
    public void setCapsulasRestantes(int capsulasRestantes) { this.capsulasRestantes = capsulasRestantes; }

    public int getDosisPorToma() { return dosisPorToma; }
    public void setDosisPorToma(int dosisPorToma) { this.dosisPorToma = dosisPorToma; }

    public int getComprimidosPorCaja() { return comprimidosPorCaja; }
    public void setComprimidosPorCaja(int comprimidosPorCaja) { this.comprimidosPorCaja = comprimidosPorCaja; }

    public int getUmbralAviso() { return umbralAviso; }
    public void setUmbralAviso(int umbralAviso) { this.umbralAviso = umbralAviso; }
    public int getDosisTomadasHoy() { return dosisTomadasHoy; }
    public void setDosisTomadasHoy(int v) { this.dosisTomadasHoy = v; }
    public String getFechaInicio() {return fechaInicio;}
    public void setFechaInicio(String fechaInicio) {this.fechaInicio = fechaInicio;}

}
