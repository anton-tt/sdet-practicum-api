package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityRequest {
    private String title;
    private Boolean verified;
    private List<Integer> important_numbers;
    private AdditionRequest addition;

}
