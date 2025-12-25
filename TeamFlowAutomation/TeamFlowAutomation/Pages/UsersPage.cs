using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI; // חובה לשימוש ב-SelectElement
using System;

namespace TeamFlow_Automation.Pages
{
    public class UsersPage : BasePage
    {
        // Selectors - זיהוי האלמנטים בדף
        private readonly By addUserButton = By.ClassName("add-btn");
        private readonly By nameInput = By.XPath("//input[@placeholder='Full Name (First Last)']");
        private readonly By emailInput = By.XPath("//input[@placeholder='email@teamflow.com']");
        private readonly By passwordInput = By.XPath("//input[@placeholder='Initial password']");
        private readonly By roleSelect = By.TagName("select"); // יש רק select אחד בטופס ההוספה
        private readonly By saveButton = By.CssSelector("button.save-btn");

        public UsersPage(IWebDriver driver) : base(driver) { }

        public void OpenAddUserForm()
        {
            Click(addUserButton);
        }
        public void AddUser(string name, string email, string password, string role)
        {
            Type(nameInput, name);
            Type(emailInput, email);
            Type(passwordInput, password);

            var select = new OpenQA.Selenium.Support.UI.SelectElement(WaitAndFind(roleSelect));
            select.SelectByText(role.Trim());

            System.Threading.Thread.Sleep(1000); 
            IWebElement saveBtn = WaitAndFind(saveButton);
            if (saveBtn.Enabled)
            {
                saveBtn.Click();
            }
            else
            {
                throw new Exception("כפתור השמירה חסום! בדקי אם הנתונים באקסל תקינים (שם מלא, אימייל של teamflow וסיסמה ארוכה).");
            }
        }

        public bool IsUserExists(string email)
        {
            By userRow = By.XPath($"//table//td[contains(text(), '{email}')]");

            try
            {
                var element = WaitAndFind(userRow);
                return element != null;
            }
            catch
            {
                return false;
            }
        }
    }
}