package demo.snack4._models;

import demo.snack3._models.SwaggerResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author noear 2022/5/3 created
 */
@Setter
@Getter
public class SwaggerInfo {
    String version;
    List<SwaggerResource> resources;
}
