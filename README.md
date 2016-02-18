# Tabbed

Tabbed is a Bukkit API for configuring the tablist to the desire of any plugin developer!

**Dependencies:**
* Any derivative of Spigot 1.8 ([PaperSpigot!](https://tcpr.ca/downloads/paperspigot))

**Demonstration:**

Soon!

## Usage
**Create Tabbed for your plugin:**

Make an instance of Tabbed for your plugin (you cannot call this twice) in `onEnable()` (typically).

```java
Tabbed tabbed = new Tabbed(this);
```

**Tab item examples:**


```java
new TextTabItem("Custom text!");
new BlankTabItem();
new PlayerTabItem(player);
```

**More complex examples:**

```java
// PlayeTabItem
new PlayerTabItem(player, PlayerTabItem.LIST_NAME_PROVIDER);
new PlayerTabItem(player, PlayerTabItem.LIST_NAME_PROVIDER, PlayerTabItem.SKIN_PROVIDER);

new PlayerTabItem(player, PlayerTabItem.DISPLAY_NAME_PROVIDER);
new PlayerTabItem(player, PlayerTabItem.DISPLAY_NAME_PROVIDER, Skins.getSkin(ChatColor.RED));

new PlayerTabItem(player, new PlayerProvider<String>() {
    @Override
    public String get(Player player) {
        return player.getName().toUpperCase();
    }
});

// TextTabItem
new TextTabItem("Some text!", 1000); // ping = 1,000 in this case
new TextTabItem("Some text!", 1000, Skins.getDefaultSkin());

new TextTabItem("Red skin :D", 0, Skins.getSkin(ChatColor.RED));
new TextTabItem("Yellow skin :O", 0, Skins.getSkin(ChatColor.YELLOW));

// BlankTabItem
new BlankTabItem(Skins.getSkin(ChatColor.RED);
```

Now you can start creating cool tablists!

## TableTabList

This tablist behaves like a table with a specified number of columns and rows. You can set specific items at a column and row. Cells
that don't have anything set are automatically filled with BlankTabItem's.

**Constructors:**

```java
tabbed.newTableTabList(player); // columns = 4
tabbed.newTableTabList(player, columns); // min width = -1
tabbed.newTableTabList(player, columns, minColumnWidth); // max width = -1
tabbed.newTableTabList(player, columns, minColumnWidth, maxColumnWidth);
```

**Usage:**

```java
TableTabList tab = tabbed.newTableTabList(player);
tab.set(col, row, item);
tab.set(0, 0, item); // top left
tab.set(new TabCell(0, 0), item); // an alias of the previous

TabItem item = tab.get(0, 0);

tab.remove(0, 0);
```

## SimpleTabList

This behaves similarly to how the normal tablist behaves. You can simply add or remove items and Minecraft handles the positioning.

**Constructors:**

```java
tabbed.newSimpleTabList(player);
tabbed.newSimpleTabList(player, 20); // limits item count to 20
tabbed.newSimpleTabList(player, 20, 5); // items with fewer than 5 characters have spaces amended to them
tabbed.newSimpleTabList(player, 20, 5, 10); // items with more than 10 chars are stripped to 10.
```

**Usage:**

```java
SimpleTabList tab = tabbed.newSimpleTabList(player);
tab.add(item);
tab.add(0, item); // inserts to first position
tab.set(0, item); // removes item at index 0, inserts this one
tab.get(4); // gets the item at index 4
```


## DefaultTabList

This is just an example of how to implement your own custom tablist. It appears identical to vanilla Minecraft. There's usually no reason you should use this, it's just a demonstration you can find [here](https://github.com/thekeenant/Tabbed/blob/master/src/main/java/com/keenant/tabbed/tablist/DefaultTabList.java).

```java
DefaultTabList tab = tabbed.newDefaultTabList(player);
```
