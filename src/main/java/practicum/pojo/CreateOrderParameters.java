package practicum.pojo;

import java.util.List;

public class CreateOrderParameters
{
    private List<String> ingredients;

    public CreateOrderParameters(List<String> ingredients)
    {
        this.ingredients = ingredients;
    }

    public CreateOrderParameters()
    {
    }

    public List<String> getIngredients()
    {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients)
    {
        this.ingredients = ingredients;
    }
}
