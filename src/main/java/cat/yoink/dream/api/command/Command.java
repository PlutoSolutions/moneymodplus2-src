package cat.yoink.dream.api.command;

public class Command {
	private String name;
	private String description;
	private String[] alias;

	public Command(String name, String description, String[] alias) {
		setName(name);
		setDescription(description);
		setAlias(alias);
	}

	public void onCommand(String arguments) {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getAlias() {
		return alias;
	}

	public void setAlias(String[] alias) {
		this.alias = alias;
	}
}