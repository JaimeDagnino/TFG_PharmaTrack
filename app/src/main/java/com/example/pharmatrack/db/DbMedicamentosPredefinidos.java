package com.example.pharmatrack.db;

import android.content.Context;

import com.example.pharmatrack.R;
import com.example.pharmatrack.entidades.Medicamentos;

import java.util.ArrayList;

public class DbMedicamentosPredefinidos {

    private final Context context;

    public DbMedicamentosPredefinidos(Context context) {
        this.context = context;
    }

    public ArrayList<Medicamentos> obtenerMedicamentos() {
        ArrayList<Medicamentos> lista = new ArrayList<>();

        lista.add(crear("Ibuprofeno 400 mg", R.drawable.ic_medicamento));
        lista.add(crear("Paracetamol 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Omeprazol 20 mg", R.drawable.ic_medicamento));
        lista.add(crear("Amoxicilina 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Enalapril 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Simvastatina 20 mg", R.drawable.ic_medicamento));
        lista.add(crear("Atorvastatina 40 mg", R.drawable.ic_medicamento));
        lista.add(crear("Metformina 850 mg", R.drawable.ic_medicamento));
        lista.add(crear("Loratadina 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Clopidogrel 75 mg", R.drawable.ic_medicamento));
        lista.add(crear("Diazepam 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Aspirina 100 mg", R.drawable.ic_medicamento));
        lista.add(crear("Furosemida 40 mg", R.drawable.ic_medicamento));
        lista.add(crear("Losartán 50 mg", R.drawable.ic_medicamento));
        lista.add(crear("Levotiroxina 50 mcg", R.drawable.ic_medicamento));
        lista.add(crear("Ramipril 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Alprazolam 0.5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Nolotil 575 mg", R.drawable.ic_medicamento));
        lista.add(crear("Dalsy 20 mg/ml", R.drawable.ic_medicamento));
        lista.add(crear("Ventolín 100 mcg", R.drawable.ic_medicamento));
        lista.add(crear("Simeticona 40 mg", R.drawable.ic_medicamento));
        lista.add(crear("Pantoprazol 40 mg", R.drawable.ic_medicamento));
        lista.add(crear("Esomeprazol 20 mg", R.drawable.ic_medicamento));
        lista.add(crear("Ranitidina 150 mg", R.drawable.ic_medicamento));
        lista.add(crear("Naproxeno 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Dexketoprofeno 25 mg", R.drawable.ic_medicamento));
        lista.add(crear("Cetirizina 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Ibuprofeno 600 mg", R.drawable.ic_medicamento));
        lista.add(crear("Paracetamol 1 g", R.drawable.ic_medicamento));
        lista.add(crear("Salbutamol 2 mg", R.drawable.ic_medicamento));
        lista.add(crear("Bisoprolol 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Amlodipino 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Metoprolol 50 mg", R.drawable.ic_medicamento));
        lista.add(crear("Candesartán 8 mg", R.drawable.ic_medicamento));
        lista.add(crear("Tramadol 50 mg", R.drawable.ic_medicamento));
        lista.add(crear("Codeína 30 mg", R.drawable.ic_medicamento));
        lista.add(crear("Trankimazin 0.25 mg", R.drawable.ic_medicamento));
        lista.add(crear("Aldactone 25 mg", R.drawable.ic_medicamento));
        lista.add(crear("Domperidona 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Clorfenamina 4 mg", R.drawable.ic_medicamento));
        lista.add(crear("Mirtazapina 30 mg", R.drawable.ic_medicamento));
        lista.add(crear("Sertralina 50 mg", R.drawable.ic_medicamento));
        lista.add(crear("Fluoxetina 20 mg", R.drawable.ic_medicamento));
        lista.add(crear("Trazodona 100 mg", R.drawable.ic_medicamento));
        lista.add(crear("Bromazepam 1.5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Olanzapina 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Quetiapina 25 mg", R.drawable.ic_medicamento));
        lista.add(crear("Lansoprazol 30 mg", R.drawable.ic_medicamento));
        lista.add(crear("Rivastigmina 3 mg", R.drawable.ic_medicamento));
        lista.add(crear("Memantina 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Insulina rápida", R.drawable.ic_medicamento));
        lista.add(crear("Insulina lenta", R.drawable.ic_medicamento));
        lista.add(crear("Insulina NPH", R.drawable.ic_medicamento));
        lista.add(crear("Clexane 40 mg", R.drawable.ic_medicamento));
        lista.add(crear("Hidroclorotiazida 25 mg", R.drawable.ic_medicamento));
        lista.add(crear("Rosuvastatina 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Fenofibrato 160 mg", R.drawable.ic_medicamento));
        lista.add(crear("Allopurinol 300 mg", R.drawable.ic_medicamento));
        lista.add(crear("Colchicina 1 mg", R.drawable.ic_medicamento));
        lista.add(crear("Aciclovir 400 mg", R.drawable.ic_medicamento));
        lista.add(crear("Valaciclovir 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Amoxicilina + clavulánico", R.drawable.ic_medicamento));
        lista.add(crear("Azitromicina 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Claritromicina 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Doxiciclina 100 mg", R.drawable.ic_medicamento));
        lista.add(crear("Metronidazol 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Levofloxacino 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Ciprofloxacino 500 mg", R.drawable.ic_medicamento));
        lista.add(crear("Gentamicina crema", R.drawable.ic_medicamento));
        lista.add(crear("Betametasona crema", R.drawable.ic_medicamento));
        lista.add(crear("Clotrimazol crema", R.drawable.ic_medicamento));
        lista.add(crear("Ketoconazol champú", R.drawable.ic_medicamento));
        lista.add(crear("Miconazol gel oral", R.drawable.ic_medicamento));
        lista.add(crear("Nistatina suspensión", R.drawable.ic_medicamento));
        lista.add(crear("Diclofenaco 50 mg", R.drawable.ic_medicamento));
        lista.add(crear("Meloxicam 15 mg", R.drawable.ic_medicamento));
        lista.add(crear("Celecoxib 200 mg", R.drawable.ic_medicamento));
        lista.add(crear("Prednisona 30 mg", R.drawable.ic_medicamento));
        lista.add(crear("Dexametasona 4 mg", R.drawable.ic_medicamento));
        lista.add(crear("Budesonida inhalador", R.drawable.ic_medicamento));
        lista.add(crear("Beclometasona nasal", R.drawable.ic_medicamento));
        lista.add(crear("Fluticasona inhalador", R.drawable.ic_medicamento));
        lista.add(crear("Montelukast 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Lorazepam 1 mg", R.drawable.ic_medicamento));
        lista.add(crear("Diazepam supositorios", R.drawable.ic_medicamento));
        lista.add(crear("Lactulosa jarabe", R.drawable.ic_medicamento));
        lista.add(crear("Plantago ovata", R.drawable.ic_medicamento));
        lista.add(crear("Rivoraxabán 20 mg", R.drawable.ic_medicamento));
        lista.add(crear("Apixabán 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Warfarina 5 mg", R.drawable.ic_medicamento));
        lista.add(crear("Digoxina 0.25 mg", R.drawable.ic_medicamento));
        lista.add(crear("Verapamilo 80 mg", R.drawable.ic_medicamento));
        lista.add(crear("Diltiazem 60 mg", R.drawable.ic_medicamento));
        lista.add(crear("Nitroglicerina spray", R.drawable.ic_medicamento));
        lista.add(crear("Isosorbida 20 mg", R.drawable.ic_medicamento));
        lista.add(crear("Fentanilo parches", R.drawable.ic_medicamento));
        lista.add(crear("Buprenorfina 5 mcg", R.drawable.ic_medicamento));
        lista.add(crear("Oxicodona 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Tapentadol 50 mg", R.drawable.ic_medicamento));
        lista.add(crear("Zolpidem 10 mg", R.drawable.ic_medicamento));
        lista.add(crear("Zopiclona 7.5 mg", R.drawable.ic_medicamento));

        return lista;
    }

    private Medicamentos crear(String nombre, int icono) {
        Medicamentos m = new Medicamentos();
        m.setNombre(nombre);
        m.setIcono(icono);
        return m;
    }
}
