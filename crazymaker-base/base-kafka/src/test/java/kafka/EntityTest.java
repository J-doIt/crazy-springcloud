package kafka;

import com.crazymaker.springcloud.kafka.start.KafkaDemoApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * GenUtils测试用例
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EntityTest {

}
