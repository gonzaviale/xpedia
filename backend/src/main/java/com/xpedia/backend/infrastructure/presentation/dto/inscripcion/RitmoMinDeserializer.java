package com.xpedia.backend.infrastructure.presentation.dto.inscripcion;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;

public class RitmoMinDeserializer extends StdDeserializer<Short> {

    public RitmoMinDeserializer() {
        super(Short.class);
    }

    @Override
    public Short deserialize(JsonParser parser, DeserializationContext context) {
        if (parser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
            throw MismatchedInputException.from(parser, Short.class, "El ritmo debe ser un número entero");
        }
        return parser.getShortValue();
    }
}
