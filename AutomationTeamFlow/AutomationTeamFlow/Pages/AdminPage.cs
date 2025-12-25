using OpenQA.Selenium;

namespace TeamFlow_Automation.Pages
{
    public class AdminPage : BasePage
    {
        private readonly By adminHeader = By.Id("admin-panel");

        public AdminPage(IWebDriver driver) : base(driver) { }

        public bool IsAdminSystem()
        {
            return WaitAndFind(adminHeader).Displayed;
        }
    }
}
