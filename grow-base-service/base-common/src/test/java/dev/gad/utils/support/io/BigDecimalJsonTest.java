package dev.gad.utils.support.io;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author LIANGQI
 * @version 1.0
 * @Desc
 */
public class BigDecimalJsonTest {

    @Test
    public void test() {
        BigDecimal val = new BigDecimal(11.1111111d);
        Stu stu = new Stu();
        stu.setVal(val);
        String json = JsonUtil.toJson(stu);
        JsonUtil.toJson(stu);
        JsonUtil.toJson(stu);
        JsonUtil.toJson(stu);
        System.out.println(json);
        Assertions.assertNotNull(JsonUtil.toBean(json, Stu.class));
    }


}

class Stu {

    @DecimalField(value = "#0.0000", roundingMode = RoundingMode.UP)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @JsonDeserialize(using = BigDecimalJsonDeserializer.class)
    private BigDecimal val;

    public BigDecimal getVal() {
        return val;
    }

    public void setVal(BigDecimal val) {
        this.val = val;
    }

}
