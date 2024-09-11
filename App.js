import React, { useState, useEffect } from 'react';
import { View, Text, Button, Image, TouchableOpacity, StyleSheet, SafeAreaView } from 'react-native';
import { NativeModules, DeviceEventEmitter } from 'react-native';

const eventsMap = {
  notification: 'notificationReceived'
};

const { NotificationModule, AppIconModule } = NativeModules;

const Notification = {
  getPermissionStatus: () => NotificationModule.getPermissionStatus(),
  requestPermission: () => NotificationModule.requestPermission(),
  getInstalledApps: () => NotificationModule.getInstalledApps(),
  on: (event, callback) => {
    const nativeEvent = eventsMap[event];
    if (!nativeEvent) throw new Error('Invalid event');
    DeviceEventEmitter.removeAllListeners(nativeEvent);
    return DeviceEventEmitter.addListener(nativeEvent, callback);
  },
};

const App = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      Notification.on('notification', (eventData) => {
        console.log("🚀 ~ Notification.on ~ eventData:", eventData);
        setData((prevData) => [...prevData, eventData]);
      });
    };
    fetchData();
  }, []);

  return (
    <View style={styles.container}>
      {data.length > 0 ? (
        data.map((value, index) => (
          <View key={index} style={styles.notificationContainer}>
            <Text style={styles.notificationText}>
              Text: {value.text || value.title}
              {'\n'}App Name: {value.appName}
            </Text>
            {value.logo && (
              <Image
                source={{ uri: `data:image/png;base64,${value.logo}` }}
                style={styles.icon}
              />
            )}
          </View>
        ))
      ) : (
        <>
          <Button onPress={() => NotificationModule.requestPermission()} title="Request Permission" />
          <Text>No Notifications</Text>
        </>
      )}
    </View>
  );
};

const AppIcon = () => {
  const [appIcon, setAppIcon] = useState(null);

  useEffect(() => {
    const packageNames = ["com.epi", "com.shopee.vn", "com.traveloka.android"];
    AppIconModule.getAppIcons(packageNames, (iconsMap) => {
      setAppIcon(iconsMap[packageNames[0]]);
    });
  }, []);

  return (
    <View style={styles.container}>
      {appIcon ? (
        <Image
          source={{ uri: `data:image/png;base64,${appIcon}` }}
          style={styles.icon}
        />
      ) : (
        <Text>No Icon Found</Text>
      )}
      <Text>App Icon</Text>
    </View>
  );
};


const AppTabs = () => {
  const [activeTab, setActiveTab] = useState('app');

  return (
    <SafeAreaView style={{ flex: 1 }}>
      <View style={styles.tabContainer}>
        <TouchableOpacity
          style={[styles.tab, activeTab === 'app' && styles.activeTab]}
          onPress={() => setActiveTab('app')}
        >
          <Text style={styles.tabText}>App</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.tab, activeTab === 'icon' && styles.activeTab]}
          onPress={() => setActiveTab('icon')}
        >
          <Text style={styles.tabText}>App Icon</Text>
        </TouchableOpacity>
      </View>
      <View style={styles.contentContainer}>
        {activeTab === 'app' && <App />}
        {activeTab === 'icon' && <AppIcon />}
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  tabContainer: {
    flexDirection: 'row',
    backgroundColor: '#f8f8f8',
    padding: 10,
  },
  tab: {
    flex: 1,
    padding: 10,
    alignItems: 'center',
    backgroundColor: '#e0e0e0',
    borderRadius: 5,
  },
  activeTab: {
    backgroundColor: '#6200ee',
  },
  tabText: {
    color: '#000',
    fontWeight: 'bold',
  },
  contentContainer: {
    flex: 1,
    padding: 16,
  },
  icon: {
    width: 50,
    height: 50,
    borderRadius: 10,
    margin: 10,
  },
  container: {
    display: 'flex',
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f0f0f0',
  },
  notificationText: {
    fontSize: 16,
    color: '#333',
    marginVertical: 5,
  },
  notificationContainer: {
    padding: 10,
    backgroundColor: '#fff',
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 2, 
    marginVertical: 5,
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '90%',
  },
});

export default AppTabs;
