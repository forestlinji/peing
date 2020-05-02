package peing.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *返回的json对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseJson<T> {

    private int status = 200;
    private String message;
    private T data;

    public ResponseJson(ResultCode code){
        this.status=code.getCode();
        this.message=code.getMsg();
    }

    public ResponseJson(ResultCode code, T data){
        this.status=code.getCode();
        this.message=code.getMsg();
        this.data=data;
    }

}
