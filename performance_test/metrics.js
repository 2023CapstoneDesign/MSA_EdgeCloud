const { Registry, Counter, Gauge, Histogram, Summary, register } = require('prom-client');
const os = require('os');


const prometheus = () => {
  const registry = new Registry();
  const instances = {};

  const create = ({ type, name, help }) => {
    let instance;

    if (type === 'counter') {
      instance = new Counter({ name, help });
    } else if (type === 'gauge') {
      instance = new Gauge({ name, help });
    } else if (type === 'histogram') {
      instance = new Histogram({ name, help });
    } else if (type === 'summary') {
      instance = new Summary({ name, help });
    }

    if (instance) {
      registry.registerMetric(instance);
      instances[name] = { type, instance };
    }
  };

  const add = ({ name, data }) => {
    if (instances[name]) {
      const { type, instance } = instances[name];

      if (type === 'counter') {
        instance.inc(data);
      } else if (type === 'gauge') {
        instance.set(data);
      } else if (type === 'histogram') {
        instance.observe(data);
      } else if (type === 'summary') {
        instance.observe(data);
      }
    }
  };

  const get = async () => {
    return {
      metrics: await registry.metrics(),
      contentType: register.contentType,
    };
  };

  return { create, add, get };
};

const Prometheus = prometheus();
Prometheus.create({
  type: 'counter',
  name: 'counter',
  help: 'random counter for test',
});
Prometheus.create({
  type: 'gauge',
  name: 'gauge',
  help: 'random gauge for test',
});
Prometheus.create({
  type: 'histogram',
  name: 'histogram',
  help: 'random histogram for test',
});
Prometheus.create({
  type: 'summary',
  name: 'summary',
  help: 'random summary for test',
});
Prometheus.create({
  type: 'gauge',
  name: 'cpu_usage',
  help: 'CPU usage in percentage',
});

Prometheus.create({
  type: 'gauge',
  name: 'memory_usage',
  help: 'Memory usage in bytes',
});

// CPU 사용량 측정 함수
function getCpuUsage() {
  const cpus = os.cpus();
  let totalIdle = 0;
  let totalTick = 0;

  for (const cpu of cpus) {
    for (const type in cpu.times) {
      totalTick += cpu.times[type];
    }
    totalIdle += cpu.times.idle;
  }

  return {
    idle: totalIdle / cpus.length,
    total: totalTick / cpus.length
  };
}

// CPU 사용량 측정 및 추가
const cpuUsage = getCpuUsage();
Prometheus.add({ name: 'cpu_usage', data: cpuUsage });

// 메모리 사용량 측정 및 추가
const memoryUsage = process.memoryUsage().rss; // RSS 메모리 사용량 (바이트 단위)
Prometheus.add({ name: 'memory_usage', data: memoryUsage });




module.exports = {
  Prometheus,
};