using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_Entities.Entities
{
    public class FilesInfo
    {
        public string Name { get; set; }
        public string Path { get; set; }
        public int Size { get; set; }
        public string Extension { get; set; }
        public DateTime CreateDate { get; set; } = DateTime.Now;

    }
}