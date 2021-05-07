package cat.yoink.dream.api.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.alt.simple.JSONArray;
import org.json.alt.simple.JSONObject;
import org.json.alt.simple.parser.ContainerFactory;
import org.json.alt.simple.parser.JSONParser;

import cat.yoink.dream.api.command.commands.FakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil extends ClassLoader {
	//рандом говно чтоб тут был не только прикол
	private static final Minecraft mc = Minecraft.getMinecraft();

	public static BlockPos GetLocalPlayerPosFloored() {
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

	public enum FacingDirection {
		North, South, East, West,
	}

	public static FacingDirection GetFacing() {
		switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
		case 0:
		case 1:
			return FacingDirection.South;
		case 2:
		case 3:
			return FacingDirection.West;
		case 4:
		case 5:
			return FacingDirection.North;
		case 6:
		case 7:
			return FacingDirection.East;
		}
		return FacingDirection.North;
	}

	public static int GetItemSlot(Item input) {
		if (mc.player == null)
			return 0;

		for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
			if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
				continue;

			ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

			if (s.isEmpty())
				continue;

			if (s.getItem() == input) {
				return i;
			}
		}
		return -1;
	}

	public static int GetRecursiveItemSlot(Item input) {
		if (mc.player == null)
			return 0;

		for (int i = mc.player.inventoryContainer.getInventory().size() - 1; i > 0; --i) {
			if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
				continue;

			ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

			if (s.isEmpty())
				continue;

			if (s.getItem() == input) {
				return i;
			}
		}
		return -1;
	}

	public static int GetItemSlotNotHotbar(Item input) {
		if (mc.player == null)
			return 0;

		for (int i = 9; i < 36; i++) {
			final Item item = mc.player.inventory.getStackInSlot(i).getItem();
			if (item == input) {
				return i;
			}
		}
		return -1;
	}

	public static int GetItemCount(Item input) {
		if (mc.player == null)
			return 0;

		int items = 0;

		for (int i = 0; i < 45; i++) {
			final ItemStack stack = mc.player.inventory.getStackInSlot(i);
			if (stack.getItem() == input) {
				items += stack.getCount();
			}
		}

		return items;
	}

	private static Entity en = null;

	public static boolean CanSeeBlock(BlockPos p_Pos) {
		if (mc.player == null)
			return false;

		if (en == null && mc.world != null)
			en = new EntityChicken(mc.player.world);

		en.setPosition(p_Pos.getX() + 0.5, p_Pos.getY() + 0.5, p_Pos.getZ() + 0.5);

		return mc.world.rayTraceBlocks(
				new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ),
				new Vec3d(en.posX, en.posY, en.posZ), false, true, false) == null;
	}

	public static boolean isCurrentViewEntity() {
		return mc.getRenderViewEntity() == mc.player;
	}

	public static boolean IsEating() {
		return mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood
				&& mc.player.isHandActive();
	}

	public static int GetItemInHotbar(Item p_Item) {
		for (int l_I = 0; l_I < 9; ++l_I) {
			ItemStack l_Stack = mc.player.inventory.getStackInSlot(l_I);

			if (l_Stack != ItemStack.EMPTY) {
				if (l_Stack.getItem() == p_Item) {
					return l_I;
				}
			}
		}

		return -1;
	}
}
