package dev.mangojellypudding.nekojs_csv;

import com.tkisor.nekojs.api.NekoJSPlugin;
import com.tkisor.nekojs.api.annotation.RegisterNekoJSPlugin;
import com.tkisor.nekojs.api.data.Binding;
import com.tkisor.nekojs.api.data.BindingsRegister;
import com.tkisor.nekojs.api.data.JSTypeAdapterRegister;
import com.tkisor.nekojs.api.event.EventGroupRegistry;
import com.tkisor.nekojs.api.recipe.RecipeNamespaceRegister;
import dev.mangojellypudding.nekojs_csv.api.CsvIO;
import dev.mangojellypudding.nekojs_csv.js.type_adapter.JsonArrayAdapter;
import dev.mangojellypudding.nekojs_csv.js.type_adapter.PathAdapter;
import lombok.NoArgsConstructor;

@RegisterNekoJSPlugin
@NoArgsConstructor
public class NekoJSCSVPlugin implements NekoJSPlugin {
    @Override
    public void registerBindings(BindingsRegister registry) {
        registry.register(Binding.of("CsvIO", new CsvIO()));
    }

    @Override
    public void registerAdapters(JSTypeAdapterRegister registry) {
        registry.register(new PathAdapter());
        registry.register(new JsonArrayAdapter());
    }

    @Override
    public void registerRecipeNamespaces(RecipeNamespaceRegister registry) {
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
    }
}
