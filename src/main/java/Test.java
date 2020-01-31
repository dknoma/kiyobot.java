import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	
	private static final Pattern REMINDER_REGEX = Pattern.compile("!remindme ([0-9])+ (s|m|h|d|w|mon) ?(.*)");
	private static final Matcher REMINDER_MATCHER = REMINDER_REGEX.matcher("").reset();
	
	public static void main(String[] args) {
		System.out.println(String.valueOf(1));
		// REMINDER_MATCHER.reset("!remindme 1 d henlo person there");
		// REMINDER_MATCHER.matches();
		//
		// System.out.println(REMINDER_MATCHER.group(3));
// 		String addExgfxRegex = "!addexgfx (\\p{XDigit}+?) (\".*\") (\".+\") (\\w+?) (.+)";
// 		String getExgfxRegex = "!getexgfx (\\p{XDigit}+?)";
// 		String getAllExgfxRegex = "!getallexgfx";
// //		String command = "!getexgfx 1A3";
// 		String command = "!addexgfx 13B \"Snowy mountain tileset; Trees, snow bunnies, blocks, logs\" \"mountain, snow\" true https://i.imgur.com/7HEGup3.png";
// 		Matcher matcher;
// 		if((matcher = Pattern.compile(addExgfxRegex).matcher(command)).matches()) {
// 			System.out.println("command: " + matcher.group());
// 			for(int i = 1; i <= matcher.groupCount(); i++) {
// 				String member = matcher.group(i);
// 				System.out.println(String.format("%d = \"%s\"", i, member));
// 			}
// 		} else if((matcher = Pattern.compile(getExgfxRegex).matcher(command)).matches()) {
// 			for(int i = 1; i <= matcher.groupCount(); i++) {
// 				String member = matcher.group(i);
// 				System.out.println(String.format("%d = \"%s\"", i, member));
// 			}
// 		} else if((matcher = Pattern.compile(getAllExgfxRegex).matcher(command)).matches()) {
// 			for(int i = 1; i <= matcher.groupCount(); i++) {
// 				String member = matcher.group(i);
// 				System.out.println(String.format("%d = \"%s\"", i, member));
// 			}
// 		}
	}


}
