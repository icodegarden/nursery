package io.github.icodegarden.nursery.servlet.web.demo.pojo.transfer;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TccDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
}
