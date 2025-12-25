using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI; // חובה לשימוש ב-SelectElement
using System;

namespace TeamFlow_Automation.Pages
{
    public class UsersPage : BasePage
    {
        // Selectors - זיהוי האלמנטים בדף
        private readonly By addUserButton = By.ClassName("add-btn");
        private readonly By nameInput = By.XPath("//label[contains(text(),'Name')]/following-sibling::input");
        private readonly By emailInput = By.XPath("//label[contains(text(),'Email')]/following-sibling::input");
        private readonly By passwordInput = By.XPath("//label[contains(text(),'Password')]/following-sibling::input");
        private readonly By roleSelect = By.XPath("//label[contains(text(),'Role')]/following-sibling::select");
        private readonly By saveButton = By.ClassName("save-btn");

        public UsersPage(IWebDriver driver) : base(driver) { }

        public void OpenAddUserForm()
        {
            Click(addUserButton);
        }

        public void AddUser(string name, string email, string password, string role)
        {
            // הקלדה לתוך השדות (תור אחר תור)
            Type(nameInput, name);
            Type(emailInput, email);
            Type(passwordInput, password);

            // טיפול ב-Dropdown - בחירת תפקיד
            IWebElement element = WaitAndFind(roleSelect);
            SelectElement select = new SelectElement(element);

            try
            {
                // ניסיון בחירה לפי הערך (Value)
                select.SelectByValue(role);
            }
            catch
            {
                // גיבוי: בחירה לפי הטקסט הנראה (Text)
                // שימי לב ל-S ול-T הגדולות - SelectByText
                string visibleText = role.Replace("ROLE_", "");
                select.SelectByValue(visibleText);
            }

            Click(saveButton);
        }

        public bool IsUserExists(string email)
        {
            // בדיקה אם האימייל מופיע בטבלה
            By userRow = By.XPath($"//table//td[contains(text(), '{email}')]");
            return Driver.FindElements(userRow).Count > 0;
        }
    }
}