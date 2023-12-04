package practicum.pojo;

public class LogoutParameters
{
    private String token;

    public LogoutParameters(String token)
    {
        this.token = token;
    }

    public LogoutParameters()
    {
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
