package com.xpedia.backend.support;

import com.xpedia.backend.domain.model.microleccion.*;

public final class MicroleccionTestData {
    private MicroleccionTestData() {}
    public static Microleccion microleccion() {
        var result = ContractData.full(Microleccion.class);
        result.setFuentes(java.util.List.of(ContractData.full(FuenteMicroleccion.class)));
        return result;
    }
}
