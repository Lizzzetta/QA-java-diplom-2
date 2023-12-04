package practicum.pojo;

public class LoginUserParameters
{
    private String email;
    private String password;

    public LoginUserParameters(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public LoginUserParameters()
    {
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
