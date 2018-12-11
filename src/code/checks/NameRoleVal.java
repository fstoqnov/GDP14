package code.checks;

import code.Marker;
import code.interfaces.SeleniumInterface;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

public class NameRoleVal extends Check{
	//buttons
	private ArrayList<String> buttonWords;
	private ArrayList<String> submitWords;
	private ArrayList<String> resetWords;
	
	//inputs
	private ArrayList<String> checkboxInputWords;
	private ArrayList<String> colorInputWords;
	private ArrayList<String> dateInputWords;
	private ArrayList<String> dateTimeLocalInputWords;
	private ArrayList<String> emailInputWords;
	private ArrayList<String> fileInputWords;
	private ArrayList<String> imageInputWords;
	private ArrayList<String> numberInputWords;
	private ArrayList<String> passwordInputWords;
	private ArrayList<String> radioInputWords;
	private ArrayList<String> rangeInputWords;
	private ArrayList<String> searchInputWords;
	private ArrayList<String> telInputWords;
	private ArrayList<String> textInputWords;
	private ArrayList<String> timeInputWords;
	private ArrayList<String> urlInputWords;
	private ArrayList<String> weekInputWords;
	
	private static final String MARKER_SUCCESS_MESSAGE = "Role of the element is properly discribed";
	private static final String MARKER_ERROR_MESSAGE = "Aria-label empty, role not discribed";
	private static final String MARKER_AMBIGUOUS_MESSAGE = "Role description can not be verified, please check the validity of role description on element";
	
    public NameRoleVal() { super("Criterion 4.1.2 Name, Role, Value");}

    @Override
    public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) { 
    	WebElement[] buttons = inter.getElementsByTagName("button");
		WebElement[] inputs = inter.getElementsByTagName("input");
		
		//buttons
		for(int i = 0; i < buttons.length; i++) {
			if(buttons[i].getAttribute("aria-label") == null) {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, buttons[i], "Buttons contain no aria-labels, ensure role is described in another way"); //no aria-label to check, no way to fail test
			} else {
				if(buttons[i].getAttribute("type").equals("button")) {
					if(!buttons[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = buttons[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : buttonWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, buttons[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, buttons[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, buttons[i], MARKER_ERROR_MESSAGE);
					}
				} else if(buttons[i].getAttribute("type").equals("submit")) {
					if(!buttons[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = buttons[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : submitWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, buttons[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, buttons[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, buttons[i], MARKER_ERROR_MESSAGE);
					}
				} else if(buttons[i].getAttribute("type").equals("reset")) {
					if(!buttons[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = buttons[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : resetWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, buttons[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, buttons[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, buttons[i], MARKER_ERROR_MESSAGE);
					}
				}		
			}
		}	
		
		//inputs
		for(int i = 0; i < inputs.length; i++) {
			if(inputs[i].getAttribute("aria-label") == null) {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], "Inputs contain no aria-labels, ensure role is described in another way"); //no aria-label to check, no way to fail test
			} else {
				if(inputs[i].getAttribute("type").equals("button")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : buttonWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("checkbox")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : checkboxInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("color")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : colorInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("date")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : dateInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("datetime-local")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : dateTimeLocalInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("email")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : emailInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("file")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : fileInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("image")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : imageInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("month")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : dateInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("number")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : numberInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("password")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : passwordInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("radio")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : radioInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("range")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : rangeInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("reset")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : resetWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("search")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : searchInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("submit")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : submitWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("tel")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : telInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("text")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : textInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("time")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : timeInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("url")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : urlInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} else if(inputs[i].getAttribute("type").equals("week")) {
					if(!inputs[i].getAttribute("aria-label").equals("")) {
						String ariaLabel = inputs[i].getAttribute("aria-label").toLowerCase();
						boolean contains = false;
						boolean foundmatch = false;
						for(String keyWord : weekInputWords) {
							if(foundmatch == false) {
								if(ariaLabel.contains(keyWord)) {
									foundmatch = true;
									contains = true;
									addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[i], MARKER_SUCCESS_MESSAGE);
								} 
							}
						} 
						if(contains == false) {
							addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[i], MARKER_AMBIGUOUS_MESSAGE);
						}
					} else {
						addFlagToElement(markers, Marker.MARKER_ERROR, inputs[i], MARKER_ERROR_MESSAGE);
					}
				} 
			}
		}
    }

    @Override
    public String[] getHTMLPass() {
    	return new String[] {				
    			"<button>",
				"<button type=\"button\" aria-label=\"close\">description</button>", 
				//work without explicitly declaring it? the button defaults to 'submit' but not in examples on wcag ????
				"<button type=\"submit\" aria-label=\"submit\">description</button>",
				"<button type=\"reset\" aria-label=\"reset\">description</button>",
				
				"<input>",
				"<input type=\"button\" aria-label=\"Close\">",
				"<input type=\"checkbox\" aria-label=\"option\">",
				"<input type=\"color\" aria-label=\"blue\">",
				"<input type=\"date\" aria-label=\"birthday\">",
				"<input type=\"datetime-local\" aria-label=\"schedule\">",
				"<input type=\"email\" aria-label=\"email\">",
				"<input type=\"file\" aria-label=\"file\">",
				"<input type=\"image\" aria-label=\"image\">",
				"<input type=\"month\" aria-label=\"month\">",
				"<input type=\"number\" aria-label=\"number\">",
				"<input type=\"password\" aria-label=\"password\">",
				"<input type=\"radio\" aria-label=\"option\">",
				"<input type=\"range\" aria-label=\"volume\">",
				"<input type=\"reset\" aria-label=\"reset\">",
				"<input type=\"search\" aria-label=\"search\">",
				"<input type=\"submit\" aria-label=\"submit\">",
				"<input type=\"tel\" aria-label=\"phone\">",
				"<input type=\"text\" aria-label=\"word\">",
				"<input type=\"time\" aria-label=\"time\">",
				"<input type=\"url\" aria-label=\"url\">",
				"<input type=\"week\" aria-label=\"week\">",
		};
    }

    @Override
    public String[] getHTMLFail() {
    	return new String[] {
    			"<button aria-label=\"\">",
				"<button type=\"submit\" aria-label=\"\">",
				"<button type=\"reset\" aria-label=\"\">",
				
				"<input type=\"button\" aria-label=\"\">",
				"<input type=\"checkbox\" aria-label=\"\">",
				"<input type=\"color\" aria-label=\"\">",
				"<input type=\"date\" aria-label=\"\">",
				"<input type=\"datetime-local\" aria-label=\"\">",
				"<input type=\"email\" aria-label=\"\">",
				"<input type=\"file\" aria-label=\"\">",
				"<input type=\"image\" aria-label=\"\">",
				"<input type=\"month\" aria-label=\"\">",
				"<input type=\"number\" aria-label=\"\">",
				"<input type=\"password\" aria-label=\"\">",
				"<input type=\"radio\" aria-label=\"\">",
				"<input type=\"range\" aria-label=\"\">",
				"<input type=\"reset\" aria-label=\"\">",
				"<input type=\"search\" aria-label=\"\">",
				"<input type=\"submit\" aria-label=\"\">",
				"<input type=\"tel\" aria-label=\"\">",
				"<input type=\"text\" aria-label=\"\">",
				"<input type=\"time\" aria-label=\"\">",
				"<input type=\"url\" aria-label=\"\">",
				"<input type=\"week\" aria-label=\"\">",
		};
    }

    //Filling lists with conventional words used for labels of this element
    @Override
    public void initialise() {
    	//strong warning
    	buttonWords = new ArrayList<String>();
    	buttonWords.add("close");
    	buttonWords.add("open");
    	buttonWords.add("search");
    	buttonWords.add("next");
    	buttonWords.add("previous");
    	buttonWords.add("go");
    	
    	//strong warning
    	submitWords = new ArrayList<String>();
    	submitWords.add("submit");
    	submitWords.add("finish");
    	submitWords.add("complete");
    	submitWords.add("update");
    	
    	//strong warning
    	resetWords = new ArrayList<String>();
    	resetWords.add("reset");
    	resetWords.add("delete");
    	
    	//weak warning
    	checkboxInputWords = new ArrayList<String>();
    	checkboxInputWords.add("option");
    	checkboxInputWords.add("order");
    	checkboxInputWords.add("choice");
    	checkboxInputWords.add("selection");
    	checkboxInputWords.add("pick");
    	
    	//weak warning
    	colorInputWords = new ArrayList<String>(); //colours found in most websites today(most often used colours according to statistics)
    	colorInputWords.add("blue");
    	colorInputWords.add("green");
    	colorInputWords.add("red");
    	colorInputWords.add("orange");
    	colorInputWords.add("yellow");
    	colorInputWords.add("white");
    	colorInputWords.add("black");
    	
    	//strong warning
    	dateInputWords = new ArrayList<String>(); //used for month input words as well due to similarities
    	dateInputWords.add("date");
    	dateInputWords.add("birthday");
    	dateInputWords.add("start");
    	dateInputWords.add("end");
    	dateInputWords.add("time");
    	dateInputWords.add("begin");
    	dateInputWords.add("born");
    	dateInputWords.add("meeting");
    	dateInputWords.add("schedule");
    	dateInputWords.add("month");
    	dateInputWords.add("year");
    	dateInputWords.add("finish");
    	
    	//weak warning
    	dateTimeLocalInputWords = new ArrayList<String>();
    	dateTimeLocalInputWords.add("schedule");
    	dateTimeLocalInputWords.add("meeting");
    	dateTimeLocalInputWords.add("dinner");
    	dateTimeLocalInputWords.add("breakfast");
    	dateTimeLocalInputWords.add("lunch");
    	
    	//strong warning
    	emailInputWords = new ArrayList<String>();
    	emailInputWords.add("email");
    	emailInputWords.add("e-mail");
    	emailInputWords.add("electronic mail");
    	emailInputWords.add("address");
    	
    	//strong warning
    	fileInputWords = new ArrayList<String>();
    	fileInputWords.add("file");
    	fileInputWords.add("choose");
    	fileInputWords.add("select");
    	fileInputWords.add("upload");
    	fileInputWords.add("picture");
    	fileInputWords.add("avatar");
    	fileInputWords.add("document");
    	fileInputWords.add("pdf");
    	fileInputWords.add("text");
    	fileInputWords.add("script");
    	fileInputWords.add("discription");
    	fileInputWords.add("cv");
    	fileInputWords.add("resume");
    	fileInputWords.add("cover letter");
    	
    	//weak warning
    	imageInputWords = new ArrayList<String>();
    	imageInputWords.add("image");
    	imageInputWords.add("icon");
    	imageInputWords.add("avatar");
    	
    	//weak warning
    	numberInputWords = new ArrayList<String>();
    	numberInputWords.add("number");
    	numberInputWords.add("amount");
    	numberInputWords.add("type");
    	numberInputWords.add("option");
    	numberInputWords.add("quantity");
    	numberInputWords.add("total");
    	
    	//strong warning
    	passwordInputWords = new ArrayList<String>();
    	passwordInputWords.add("password");
    	passwordInputWords.add("pass");
    	
    	//weak warning
    	radioInputWords = new ArrayList<String>();
    	radioInputWords.add("option");
    	radioInputWords.add("selection");
    	radioInputWords.add("pick");
    	
    	//weak warning
    	rangeInputWords = new ArrayList<String>();
    	rangeInputWords.add("volume"); 
    	rangeInputWords.add("size");
    	rangeInputWords.add("selection");
    	rangeInputWords.add("scroll"); 
    	rangeInputWords.add("point");
    	rangeInputWords.add("min");
    	rangeInputWords.add("max"); 
    	
    	//strong warning
    	searchInputWords = new ArrayList<String>();
    	searchInputWords.add("search"); 
    	searchInputWords.add("browse");
    	searchInputWords.add("explore");
    	searchInputWords.add("query");
    	
    	//strong warning
    	telInputWords = new ArrayList<String>();
    	telInputWords.add("phone");
    	telInputWords.add("telephone"); 
    	telInputWords.add("tel"); 
    	telInputWords.add("mobile");
    	
    	//weak warning
    	textInputWords = new ArrayList<String>();
    	textInputWords.add("word");
    	textInputWords.add("name");
    	textInputWords.add("confirm");
    	textInputWords.add("yes");
    	textInputWords.add("no");
    	
    	//weak warning
    	timeInputWords = new ArrayList<String>();
    	timeInputWords.add("time");
    	timeInputWords.add("start");
    	timeInputWords.add("end");
    	timeInputWords.add("begin");
    	timeInputWords.add("finish");
    	timeInputWords.add("duration");
    	
    	//strong warning
    	urlInputWords = new ArrayList<String>();
    	urlInputWords.add("url");
    	urlInputWords.add("web");
    	urlInputWords.add("address");
    	urlInputWords.add("link");
    	
    	//weak warning
    	weekInputWords = new ArrayList<String>();
    	weekInputWords.add("week");
    	weekInputWords.add("start");
    	weekInputWords.add("end");
    	weekInputWords.add("begin");
    	weekInputWords.add("finish");
    	
    }

}
