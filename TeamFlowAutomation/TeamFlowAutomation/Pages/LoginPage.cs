using OpenQA.Selenium;

namespace TeamFlow_Automation.Pages
{
    public class LoginPage : BasePage
    {
        private readonly string url = "http://localhost:4200/login";

        // CssSelector
        private readonly By emailInput = By.CssSelector("input[type='email']");
        private readonly By passwordInput = By.CssSelector("input[type='password']");

        private readonly By loginButton =
            By.XPath("//button[@type='submit' and text()='Login']");

        private readonly By errorMessage =
            By.XPath("//p[contains(@class,'error')]");

        public LoginPage(IWebDriver driver) : base(driver) { }

        public void GoTo() => Driver.Navigate().GoToUrl(url);

        public void Login(string email, string password)
        {
            Type(emailInput, email);
            Type(passwordInput, password);
            Click(loginButton);
        }

        public bool IsErrorDisplayed()
        {
            return Driver.FindElements(errorMessage).Count > 0;
        }
    }
}
