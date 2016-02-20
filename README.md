# Tabbed

![http://i.imgur.com/ftS7FkG.png](http://i.imgur.com/ftS7FkG.png)

Tabbed is a Bukkit API for configuring the tablist to the desire of any plugin developer! Make tab lists the
way in which you always desired.

**Dependencies:**
* Any derivative of Spigot 1.8 ([PaperSpigot!](https://tcpr.ca/downloads/paperspigot))
* [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

**Demonstration:**

Soon!

## Usage
**Create Tabbed for your plugin:**

Make an instance of Tabbed for your plugin (you cannot call this twice) in `onEnable()`.
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

### Things to know!

* Tab lists are associated per player. This means you must create a new tab list every time a user joins (`PlayerJoinEvent` works just fine).
* You can get a player's tab list with `tabbed.getTabList(player)`
* You can remove a player's custom tab list with `tabbed.destroyTabList(player)` or `tabbed.destroyTabList(tab)`
* `update()` is called on tablists every second. This simply checks for updates to dynamic elements that exist in tab items (such as player pings). You can manually call it if you so desire (such as if you call `setPlayerListName()` and wish to update their tab item
immediately).

Now you can start creating cool tablists!


### TitledTabList

This tab list doesn't modify the behavior of the items in the tab list, it jus allows you to change the header and footer. **All
tab lists `extend TitledTabList` which means you can access these methods no matter the tab list type.**

**Constructor:**
```java
tabbed.newTitledTabList(player);
```

**Usage:**
```java
TitledTabList tab = tabbed.newTitledTabList(player);
tab.setHeader("The tab list header!");
tab.setFooter("The tab list footer :O");

// Better to send this (one packet)
tab.setHeaderFooter("The tab list header!", "The tab list footer :O");

// Reset
tab.resetHeader();
tab.resetFooter();
tab.resetHeaderFooter();

// Getters
String header = tab.getHeader();
String footer = tab.getFooter();
```

### TableTabList

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
tab.set(new TableCell(0, 0), item); // an alias of the previous

TabItem item = tab.get(0, 0);
TabItem item = tab.get(new TableCell(0, 0));

tab.remove(0, 0);
tab.remove(new TableCell(0,0)); // same thing

// Fill a box
List<TabItem> items = new ArrayList<TabItem>();
items.add(new TextTabItem("This will be at 0,0"));
items.add(new TextTabItem("This will be at 1,0"));
items.add(new TextTabItem("This will be at 0,1"));
items.add(new TextTabItem("This will be at 1,1"));
tab.fill(0, 0, 1, 1, items, TableCorner.TOP_LEFT, FillDirection.HORIZONTAL);
```

### SimpleTabList
This behaves similarly to how the normal tablist behaves. You can simply add or remove items and Minecraft handles the positioning.

**Constructors:**
```java
tabbed.newSimpleTabList(player);
tabbed.newSimpleTabList(player, maxItems); // limits item count (default is MC maximum, aka 80 or 4x20)
tabbed.newSimpleTabList(player, maxItems, minColumnWidth); // add spaces to items until min width
tabbed.newSimpleTabList(player, maxItems, minColumnWidth, maxColumnWidth); // remove characters until max width
```

**Usage:**
```java
SimpleTabList tab = tabbed.newSimpleTabList(player);
tab.add(item);
tab.add(0, item); // inserts to first position
tab.set(0, item); // removes item at index 0, inserts this one
tab.get(4); // gets the item at index 4
```

### DefaultTabList
This is just an example of how to implement your own custom tablist. It appears identical to vanilla Minecraft. There's usually no reason you should use this, it's just a demonstration you can find [here](https://github.com/thekeenant/Tabbed/blob/master/src/main/java/com/keenant/tabbed/tablist/DefaultTabList.java).
```java
DefaultTabList tab = tabbed.newDefaultTabList(player);
```

## Batch updating

Tabbed sends packets only when it is necessary: it runs checks to see if there are differences between what the client currently sees
and what is being sent. Tabbed doesn't know, on the other hand, when you are sending a bunch of new tab items in a row. For example if you have a loop like:
```java
int i = 0;
for (Player player : Bukkit.getOnlinePlayers()) {
    tabbed.set(i, new PlayerTabItem(player));
    i++;
}
```
It will send up to `2 * Bukkit.getOnlinePlayers().length` packets to the player (update name + ping). This might cause some blinking for the client. It is smarter to batch send these packets and reduce it to a maximum of `4` packets sent like so:
```java
tabbed.setBatchUpdate(true);
int i = 0;
for (Player player : Bukkit.getOnlinePlayers()) {
    tabbed.set(i, new PlayerTabItem(player));
}
tabbed.batchUpdate(); // sends the packets!
tabbed.setBatchUpdate(false); // optional
```
No blinking any more (except skins, that's just Minecraft downloading/reading the skin when it isn't cached).
