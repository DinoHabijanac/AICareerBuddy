using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_Entities.Entities
{
    public class Resume
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Email { get; set; }
        public string PhoneNumber { get; set; }
        public DateTime DateOfBirth { get; set; }
        public string AboutMe { get; set; }
        public List<string> Projects { get; set; } = new List<string>();
        public string WorkExperience { get; set; }
    }
}
