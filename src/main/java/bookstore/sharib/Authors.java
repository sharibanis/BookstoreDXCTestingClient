package bookstore.sharib;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "authors")
public class Authors {
	  @Id
	  @NotNull
	  @Column(name="name")
	  private String name;
	  public String getName() {
		return name;
	}

	  public void setName(String name) {
		  this.name = name;
	  }

	  @Column(name="birthday") 	
	  private String birthday;

	  public String getBirthday() {
		return birthday;
	}

	  public void setBirthday(String birthday) {
		  this.birthday = birthday;
	  }

	  private static final Logger log = LoggerFactory.getLogger(Authors.class);

	  Authors() {
	  }

	  Authors(String name, String birthday) {
	    this.name = name;
	    this.birthday = birthday;
	  }

	@Override
	public String toString() {
		return "Authors [name=" + name + ", birthday=" + birthday + "]";
	}

}