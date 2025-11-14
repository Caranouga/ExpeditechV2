
---

# Contributing to Expeditech

Thank you for your interest in contributing to **Expeditech**! Your help is invaluable in making this project better and more fun for everyone. This guide will help you get started with contributing, whether you're fixing bugs, adding features, or improving documentation.

---

## 🧰 Requirements

* **Minecraft Version**: 1.16.5
* **Mod Loader**: [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/)
* **Java Version**: 8 (or compatible with Forge 1.16.5)
* **IDE**: IntelliJ IDEA (recommended), Eclipse or any other IDE
* **Build Tool**: [Gradle](https://gradle.org/) (comes with the Forge MDK)

---

## 🧑‍💻 Getting Started

1. **Fork the repository** and clone your fork:

   ```bash
   git clone https://github.com/YOUR_USERNAME/ExpeditechV2.git
   cd ExpeditechV2
   ```

2. **Import into your IDE** using the `build.gradle` file.
   In IntelliJ:

    * Select "Import Project" and choose `build.gradle`.
    * Let it download dependencies.

3. **Setup your run configs** (`Run -> Edit Configurations...`) for:

    * `runClient`
    * `runServer`

4. **Build the mod** with:

   ```bash
   ./gradlew build
   ```

   The built `.jar` will be in `build/libs/`.

---

## ✨ How to Contribute

### ✅ Issues

* Check the [issue tracker](https://github.com/Caranouga/Expeditech/issues) for open issues or bugs.
* Comment on the issue to claim it or discuss your plan.
* You're welcome to open new issues for bugs or feature suggestions.

### 🛠️ Code Contributions

* Create a new branch for your changes:
  `git checkout -b feature/my-feature` or `bugfix/my-bug`.
* Follow the code style and architecture already used in the project.
* Write clear commit messages.
* Test your changes thoroughly.
* Open a **Pull Request** (PR) to the `main` branch with a clear description of what you changed.

### 📄 Documentation

* You can help by improving in-code comments or adding external documentation (e.g., README, Wiki, Javadoc).
* Keep documentation changes in sync with code changes.

---

## 🧪 Dev Notes

* Avoid using external mods/APIs unless discussed beforehand—Expeditech aims to be self-contained.

---

## 💬 Communication

* For help or discussion, open a GitHub issue or discussion.
* PR reviews may take time—please be patient and open to feedback.

---

## 📝 License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).