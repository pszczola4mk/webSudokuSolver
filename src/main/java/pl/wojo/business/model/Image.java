package pl.wojo.business.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Image {

	private String name;
	private String data;

	public Image(String name, String data) {
		this.name = name;
		this.data = data;
	}
}
