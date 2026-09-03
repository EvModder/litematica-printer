plugins {
    id("dev.kikugie.stonecutter")
}

// The checked-in sources are the 26.2 implementation.
stonecutter.active("26.2")

stonecutter.parameters {
    replacements {
        // Minecraft renamed these between 1.21.4 and the unobfuscated 26.x line.
        // Differences that change control flow live behind //? conditionals instead.
        string(current.parsed < "26.1") {
            replace("ContainerInput", "ClickType")
            replace("handleContainerInput", "handleInventoryMouseClick")
            replace(".getNonEquipmentItems()", ".items")
            replace(".getSelectedSlot()", ".selected")
            replace(".setSelectedSlot(inv.getSuitableHotbarSlot());", ".selected = inv.getSuitableHotbarSlot();")
            replace(".setSelectedSlot(hotbarSlot);", ".selected = hotbarSlot;")
            replace(".setSelectedSlot(nextSlot);", ".selected = nextSlot;")
            replace(".setSelectedSlot(slot);", ".selected = slot;")
            replace(".gui.screen()", ".screen")
            replace("FarmlandBlock", "FarmBlock")
            replace(".mainCamera().yRot()", ".getMainCamera().getYRot()")
            replace(".invertMouseY()", ".invertYMouse()")

            // Litematica took Path instead of File from 0.28 onwards.
            replace("import java.nio.file.Path;", "import java.io.File;")
            replace("invokeConstructor(Path file,", "invokeConstructor(File file,")
            replace("invokeConstructor(file.toPath(),", "invokeConstructor(file,")
            replace("writeToFile(out.toPath(),", "writeToFile(out,")
            replace("new File(out, fileName + \".litematic\").toPath()", "new File(out, fileName + \".litematic\")")
            replace("getFile().toFile()", "getFile()")
            replace("getDirectory().toFile()", "getDirectory()")

            replace("Minecraft.getInstance().getWindow(),", "Minecraft.getInstance().getWindow().getWindow(),")
            replace("minecraft.player.sendSystemMessage(", "minecraft.gui.getChat().addMessage(")
        }
    }
}
