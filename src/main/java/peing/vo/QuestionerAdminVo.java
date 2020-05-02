package peing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import peing.pojo.Question;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionerAdminVo extends Question {
    private String questionerName;
}
