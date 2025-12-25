using OpenQA.Selenium;
using System;


namespace TeamFlow_Automation.Pages
{
    internal class SelectElement
    {
        private IWebElement webElement;

        public SelectElement(IWebElement webElement)
        {
            this.webElement = webElement;
        }

        internal void SelectByValue(string role)
        {
            throw new NotImplementedException();
        }
    }
}