export default {

  // 首页
  getHomeList() {
    return home.list;
  }




}

var home = {
  list: [{
      name: "iBeacon",
      address: "19:18:FC:15:10:46",
      battery: 100,
      rssi: -60,
      broadcastInterval: 330,
      thoroughfares: [{
        type: "UID",
        instanceId: "0000",
        namespaceId: "07BD01FC07C501FC0700"
      }],
      beacon: {
        uuid: "1918FC80B1113441A9ACB1001C2FE510",
        major: 20001,
        minor: 22222,
        calibrationDistance: -79,
      },
      acc: {
        xAxis: "1.00",
        yAxis: "1.00",
        yAxis: "1.00",
      },
    },
    {
      name: "Unnamed",
      address: "19:18:FC:15:10:47",
      battery: 100,
      rssi: -73,
      broadcastInterval: 330,
      beacon: {
        uuid: "1918FC80B1113441A9ACB1001C2FE510",
        major: 20001,
        minor: 22224,
        calibrationDistance: -78,
      },
      acc: {
        xAxis: "1.30",
        yAxis: "1.30",
        yAxis: "1.30",
      },
    },
    {
      name: "Unnamed",
      address: "19:18:FC:15:10:48",
      battery: 100,
      rssi: -75,
      broadcastInterval: 330,
      beacon: {
        uuid: "1918FC80B1113441A9ACB1001C2FE510",
        major: 20001,
        minor: 32547,
        calibrationDistance: -78,
      },
      acc: {
        xAxis: "1.20",
        yAxis: "1.20",
        yAxis: "1.20",
      },
    },
  ]
}
// 首页设备列表
