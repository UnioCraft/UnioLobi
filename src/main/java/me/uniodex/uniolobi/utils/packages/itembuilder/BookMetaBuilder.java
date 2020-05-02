package me.uniodex.uniolobi.utils.packages.itembuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import me.uniodex.uniolobi.utils.packages.itembuilder.util.AccessUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

@SuppressWarnings({"unchecked", "serial"})
public class BookMetaBuilder extends MetaBuilder {

	public BookMetaBuilder() {
	}

	public BookMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public BookMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected BookMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private BookMeta getMeta() {
		return (BookMeta) this.meta;
	}

	@Override
	public BookMetaBuilder fromConfig(ConfigurationSection section, boolean translateColors) {
		super.fromConfig(section, translateColors);

		if (section.contains("author")) {
			withAuthor(format(translateColors(section.getString("author"), translateColors)));
		}

		if (section.contains("title")) {
			withTitle(format(translateColors(section.getString("title"), translateColors)));
		}

		if (section.contains("pages")) {
			List<String> pageStrings = section.getStringList("pages");
			if (translateColors || !formatMap.isEmpty()) {
				List<String> translated = new ArrayList<>();
				for (String s : pageStrings) {
					translated.add(format(translateColors(s, translateColors)));
				}
				withPages(translated);
			} else { withPages(pageStrings); }
		}

		return this;
	}

	@Override
	public ConfigurationSection toConfig(ConfigurationSection section) {
		section = super.toConfig(section);

		section.set("author", getMeta().getAuthor());
		section.set("title", getMeta().getTitle());
		section.set("pages", new ArrayList<String>() {
			{
				for (String page : getMeta().getPages()) {
					add(page);
				}
			}
		});

		return section;
	}

	/**
	 * Change the title of the book
	 *
	 * @param title title
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withTitle(String title) {
		validateInit();
		getMeta().setTitle(title);
		return this;
	}

	/**
	 * Change the author of the book
	 *
	 * @param author author
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withAuthor(String author) {
		validateInit();
		getMeta().setAuthor(author);
		return this;
	}

	/**
	 * Change the specific page of the book
	 *
	 * @param page    page index
	 * @param content page content
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withPage(int page, String content) {
		validateInit();
		getMeta().setPage(page, content);
		return this;
	}

	/**
	 * Change the specific page of the book
	 *
	 * @param page      page index
	 * @param component {@link BaseComponent} content of the page
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withPage(int page, BaseComponent component) {
		validateInit();

		try {
			List<Object> pages = (List<Object>) CraftMetaBook_pages.get(getMeta());
			pages.set(page - 1, deseriliazeMessage(ComponentSerializer.toString(component)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	/**
	 * Set the pages of the book
	 *
	 * @param pages List of pages
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withPages(List<String> pages) {
		validateInit();
		getMeta().setPages(pages);
		return this;
	}

	/**
	 * Add a page to the book
	 *
	 * @param pages Array of pages to add
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withPage(String... pages) {
		validateInit();
		getMeta().addPage(pages);
		return this;
	}

	/**
	 * Add a page to the book
	 *
	 * @param components Array of {@link BaseComponent} pages to add
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withPage(BaseComponent... components) {
		validateInit();

		try {
			List<Object> pages = (List<Object>) CraftMetaBook_pages.get(getMeta());
			for (BaseComponent component : components) {
				pages.add(deseriliazeMessage(ComponentSerializer.toString(component)));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	/**
	 * Set the pages of the book
	 *
	 * @param pages Array of pages to set
	 * @return the BookMetaBuilder
	 */
	public BookMetaBuilder withPages(String... pages) {
		validateInit();
		getMeta().setPages(pages);
		return this;
	}

	/**
	 * @return the built {@link BookMeta}
	 */
	@Override
	public BookMeta build() {
		return (BookMeta) super.build();
	}

	String serializeMessage(Object component) {
		String serialized = "{}";
		try {
			serialized = (String) AccessUtil.setAccessible(Reflection.getMethod(ChatSerializer, "a", new Class[] { IChatBaseComponent })).invoke(null, component);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialized;
	}

	Object deseriliazeMessage(String serialized) {
		try {
			return AccessUtil.setAccessible(Reflection.getMethod(ChatSerializer, "a", new Class[] { String.class })).invoke(null, serialized);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	Class<?> CraftMetaBook       = Reflection.getOBCClass("inventory.CraftMetaBook");
	Class<?> IChatBaseComponent  = Reflection.getNMSClass("IChatBaseComponent");
	Class<?> ChatSerializer      = Reflection.getNMSClass("IChatBaseComponent$ChatSerializer");
	Field    CraftMetaBook_pages = Reflection.getField(CraftMetaBook, "pages");
}
