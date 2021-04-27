package pl.wojo.business;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.wojo.business.model.Image;

@FeignClient(name="image", url = "${service.url}")
public interface ImageClient {

	@RequestMapping(value = "/resolve", method = RequestMethod.POST)
	String resolveSudoku(@RequestBody Image image);
}