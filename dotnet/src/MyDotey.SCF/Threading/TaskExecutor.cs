using System;
using System.Threading;
using System.Threading.Tasks;

using NLog;

namespace MyDotey.SCF.Threading
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     * 
     * Simple Thread Pool
     */
    public class TaskExecutor : IDisposable
    {
        private static Logger LOGGER = LogManager.GetCurrentClassLogger(typeof(TaskExecutor));

        public virtual void run(Action task)
        {
            if (task == null)
                throw new ArgumentNullException("task is null");

            Task.Run(task).ContinueWith(t =>
            {
                if (t.Exception != null)
                    LOGGER.Error(t.Exception, "task failed");
            });
        }

        public virtual void Dispose()
        {

        }
    }
}