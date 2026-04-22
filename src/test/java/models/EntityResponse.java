package models;

import lombok.Data;

import java.util.List;

@Data
public class EntityResponse {
    private Integer id;
    private String title;
    private Boolean verified;
    private List<Integer> important_numbers;
    private AdditionResponse addition;

}
