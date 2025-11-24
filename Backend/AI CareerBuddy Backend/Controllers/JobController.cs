using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_BussinesLayer.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;

namespace AI_CareerBuddy_Backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class JobController : ControllerBase
    {
        private readonly IJobService JobService;

        public JobController(JobService jobService)
        {
            JobService = jobService;
        }

        // GET: api/Job
        [HttpGet]
        public ActionResult<IEnumerable<JobListing>> Get()
        {
            var jobs = JobService.GetJobs();
            return Ok(jobs);
        }

        // GET api/Job/5
        [HttpGet("{id}")]
        public ActionResult<JobListing> Get(int id)
        {
            var job = JobService.GetJob(id);
            if (job == null) return NotFound();
            return Ok(job);
        }

        // POST api/Job
        [HttpPost]
        public ActionResult<JobListing> Post([FromBody] JobListing job)
        {
            if (job == null) return BadRequest();
            var created = JobService.PostJob(job);
            if (created == null) return BadRequest();
            return CreatedAtAction(nameof(Get), new { id = created.Id }, created);
        }

        // PUT api/Job/5
        [HttpPut("{id}")]
        public ActionResult<JobListing> Put(int id, [FromBody] JobListing job)
        {
            if (job == null || job.Id != id) return BadRequest();
            var updated = JobService.PutJob(job);
            if (updated == null) return NotFound();
            return Ok(updated);
        }

        // DELETE api/Job/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            var deleted = JobService.DeleteJob(id);
            if (deleted == false) return NotFound();
            return NoContent();
        }
    }
}