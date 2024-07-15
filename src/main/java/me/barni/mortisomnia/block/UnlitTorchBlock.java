package me.barni.mortisomnia.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractTorchBlock;

public class UnlitTorchBlock extends AbstractTorchBlock {
    public UnlitTorchBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends AbstractTorchBlock> getCodec() {
        return null;
    }
}

