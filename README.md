# Tabbed

[![Build Status](https://travis-ci.org/thekeenant/tabbed.svg?branch=master)](https://travis-ci.org/thekeenant/tabbed)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0d18390d22764a86bb77dc65208319d5)](https://www.codacy.com/app/thekeenant/tabbed?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=thekeenant/tabbed&amp;utm_campaign=Badge_Grade)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-250%20downloads-yellow.svg)](https://www.spigotmc.org/resources/tabbed.18871/)

Tabbed is a Bukkit API for configuring the tablist for your users. Each slot is configurable: set the icon, ping, and text
for each tab list item to your liking.

**This library is not actively maintained, but if you are interested in contributing, go right ahead!**

**Dependencies:**
* Any derivative of Bukkit 1.8 or 1.9
* [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

**Demonstration:**

![http://i.imgur.com/hbO4Nbq.gif](http://i.imgur.com/hbO4Nbq.gif)

## Maven/Gradle

See: [Jitpack](https://jitpack.io/#thekeenant/tabbed/v1.8) for specifics.

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.thekeenant</groupId>
    <artifactId>tabbed</artifactId>
    <version>v1.8</version>
  </dependency>
</dependencies>
```


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
new PlayerTabItem(player, PlayerTabItem.DISPLAY_NAME_PROVIDER, Skins.getDot(ChatColor.RED));

new PlayerTabItem(player, new PlayerProvider<String>() {
    @Override
    public String get(Player player) {
        return player.getName().toUpperCase();
    }
});

// TextTabItem
new TextTabItem("Some text!", 1000); // ping = 1,000 in this case
new TextTabItem("Some text!", 1000, Skins.DEFAULT_SKIN);

new TextTabItem("Red skin :D", 0, Skins.getDot(ChatColor.RED));
new TextTabItem("Yellow skin :O", 0, Skins.getDot(ChatColor.YELLOW));
new TextTabItem("An Enderman!", 0, Skins.getMob(EntityType.ENDERMAN));

// BlankTabItem
new BlankTabItem(Skins.getSkin(ChatColor.RED);
```

### Custom skin heads

To be able to use your custom skin's head you will need a few things.
* The texture value and the texture signature
* The skin with the head you want (a player skin)

To get the texture value or signature you can use https://mineskin.org, it will give you the texutre value and signature.
You can also get a player's skin texture value and signature using Mojang's session servers (https://sessionserver.mojang.com/session/minecraft/profile/PLAYER-UUID-HERE?unsigned=false)

**Constructor:**
```java
Skin = new Skin("Texture value here", "Texture signature here");
```

**Usage:**
```java
// The custom head using texture value and signature
Skin SPEECH_BUBBLE_CHAT = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY3MjYxMTE5OTc3MCwKICAicHJvZmlsZUlkIiA6ICJhNTkyMjkwNDVjMjI0MGUyOTM0ZjMxZWFjMzNiY2IzNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTbHVnRGVhbGVyQWdhaW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ2NDVhZGFkOWRlMDgyNDc5NTVkN2UwYjc3MWJiMWY4MjZmOTI1MWY2ZTA5MTNmYmJjNjdhODAxYzJjMTU0ZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "dZKZoFGwqs7WQ2zhQNiN8UEMRVdRNOr1kzhYBVGutOledr7VmxGCIfKNNil4Gs/zmm8+/jCZPvJn0/39XAUVuCGw6Mhr4+2Vatz1ivpC9JfT4Czs8op9JsVcYZ/pLh5j/CxpgQ3/xn3tYuw09aHwZPDZW+FXt8zMYKNzrXiBQrD2jGYe9FHSWmwghYSHAx7DVbg20B5ajpvGejMzvA2PBSQFe7YX/7umpnIXrst8LcDP6EdhlmNgBZ3x51H88cn9QqctXjPVwweW81B0ufHOTrbncAGFgCUykfny0dlcYYdiulm0dTNcSFAEzqbqNfNN6+XveEiss+POyQt+g6Vr9kfMokgk51x+gtBHnre7MAHBTfcANR5A1PMfQOrFJUCFt42MXYp2Y3iHtYAho86jOlKs/nX2rSS9uOJ5ov70aIYyNNe+5w4kll5tZmaaE16fqCY0oKOICzAzOWe12tOFPTlpLyM1+2UBdwGjMUaJmzG6X0YiS8JvH73HuAmVM/wQuZ/WRBnOEvcqZjUPyqkCvP3vNIqecPeXZvdDz7IJJ0Q2/iRZdE4m/mnbvItcpeZvCQeH058AHqGpYY7Ei0SwdU568EasShYwoOqgruC0NP2O7dAo/pQjEQiEZnGViDUFUaz0bbXpIvnDuBW+eyuEenWAN4ASVgtNSoy/F6093Bo=");

TitledTabList tab = tabbed.newTitledTabList(player);
tab.set(0, 0, new TextTabItem("Custom head!", 0, SPEECH_BUBBLE_CHAT));
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
