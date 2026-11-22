package br.com.redeapoio.common;

import java.util.Arrays;
import java.util.List;

public final class Csv {
    private Csv() {}

    public static List<String> dividir(String valor) {
        if (valor == null || valor.isBlank()) {
            return List.of();
        }
        return Arrays.stream(valor.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static String juntar(List<String> valores) {
        return valores == null || valores.isEmpty() ? null : String.join(",", valores);
    }
}
