package dev.danvega.donuts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Donut(
        @JsonView(Views.Summary.class)
        String type,
        @JsonView(Views.Public.class)
        Glaze glaze,
        @JsonView(Views.Public.class)
        List<String> toppings,
        @JsonView(Views.Summary.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "$#.##")
        BigDecimal price,
        @JsonView(Views.Public.class)
        Boolean isVegan,
        @JsonView(Views.Internal.class)
        Integer calories,
        @JsonView(Views.Internal.class)
        LocalDateTime bakedAt
) {
    public enum Glaze {
        CHOCOLATE,
        VANILLA,
        STRAWBERRY,
        MAPLE,
        CINNAMON_SUGAR,
        POWDERED_SUGAR,
        NONE
    }
}