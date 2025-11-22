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
        private readonly IJobService _jobService;

        public JobController(JobService jobService)
        {
            _jobService = jobService;
        }

        // GET: api/Job
        [HttpGet]
        public ActionResult<IEnumerable<JobListing>> Get()
        {
            var jobs = _jobService.GetJobs();
            return Ok(jobs);
        }

        // GET api/Job/5
        [HttpGet("{id}")]
        public ActionResult<JobListing> Get(int id)
        {
            var job = _jobService.GetJob(id);
            if (job == null) return NotFound();
            return Ok(job);
        }

        // POST api/Job
        [HttpPost]
        public ActionResult<JobListing> Post([FromBody] JobListing job)
        {
            if (job == null) return BadRequest();
            var created = _jobService.PostJob(job);
            if (created == null) return BadRequest();
            return CreatedAtAction(nameof(Get), new { id = created.Id }, created);
        }

        // PUT api/Job/5
        [HttpPut("{id}")]
        public ActionResult<JobListing> Put(int id, [FromBody] JobListing job)
        {
            if (job == null || job.Id != id) return BadRequest();
            var updated = _jobService.PutJob(job);
            if (updated == null) return NotFound();
            return Ok(updated);
        }

        // DELETE api/Job/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            var deleted = _jobService.DeleteJob(id);
            if (deleted == null) return NotFound();
            return NoContent();
        }
    }
}