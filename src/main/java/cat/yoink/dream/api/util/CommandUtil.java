package cat.yoink.dream.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUtil {

	public static void sendChatMessage(String message) {
		sendRawChatMessage("&7[&amoneymod+ 2&7] &r" + message);
	}

	public static void sendRawChatMessage(String message) {
		EntityPlayerSP SPPlayer = Minecraft.getMinecraft().player;
		SPPlayer.sendMessage(new ChatMessage(message));
	}

	public static class ChatMessage extends TextComponentBase {

		String text;

		public ChatMessage(String text) {

			Pattern p = Pattern.compile("&[0123456789abcdefrlosmk]");
			Matcher m = p.matcher(text);
			StringBuffer sb = new StringBuffer();

			while (m.find()) {
				String replacement = "\u00A7" + m.group().substring(1);
				m.appendReplacement(sb, replacement);
			}

			m.appendTail(sb);

			this.text = sb.toString();
		}

		public String getUnformattedComponentText() {
			return text;
		}

		@Override
		public ITextComponent createCopy() {
			return new ChatMessage(text);
		}

	}
}
