### Setup

1. Install IntelliJ IDEA 2024.2 EAP / Dev
2. Enable K2 mode in the Settings
![img.png](img.png)
3. Restart the IDE and un-check `kotlin.k2.only.bundled.compiler.plugins.enabled`
![img_1.png](img_1.png)
4. Sync the project 

### Content

Multiple examples are provided: 

1. [WowAH.kt](src%2Fmain%2Fkotlin%2FWowAH.kt) starts with defined DataSchema 
2. [JetBrainsRepositories.kt](src%2Fmain%2Fkotlin%2FJetBrainsRepositories.kt) infers DataSchema from the data
3. [SupportedAPI.kt](src%2Fmain%2Fkotlin%2FSupportedAPI.kt) provides an overview for things that plugin can do