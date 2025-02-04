# AndroidAPS + GH Actions = ❤️

Aside from being a regular fork of [nightscout/AndroidAPS](https://github.com/nightscout/AndroidAPS) repository
with `master` branch as well as [some patches for my own use](https://github.com/Jackenmen/AndroidAPS/compare/master...Jackenmen:AndroidAPS-public-fork:jack_patches),
this repository contains GitHub Actions workflows for:
- generating APK from selected repository's branches ([generate_apk.yaml](https://github.com/Jackenmen/AndroidAPS/blob/jack/ci-generate-app/.github/workflows/generate_apk.yaml))
- automatically pulling changes from upstream repository and triggering the above build workflow ([pull_from_upstream.yaml](https://github.com/Jackenmen/AndroidAPS/blob/jack/ci-generate-app/.github/workflows/pull_from_upstream.yaml))
- utilities for making the initial setup easier

You can use these for building AAPS for yourself by importing this project as a **private** GitHub repository and making a few changes.

> [!CAUTION]
> DO NOT use this repository's workflow on public repository. Due to regulations concerning medical devices,
> it is likely **illegal** to run this on a public repository as it will lead to ready-to-use APK files
> uploaded publicly.
>
> See ["Can I just download the AAPS installation file?" FAQ from AndroidAPS docs](https://androidaps.readthedocs.io/en/latest/UsefulLinks/FAQ.html#can-i-just-download-the-aaps-installation-file) for more information.

## How to use this?

### Setup

1. Import this repository:

    - Go to https://github.com/new/import
    - Paste the following in the URL field:
      ```
      https://github.com/Jackenmen/AndroidAPS-public-fork
      ```
    - Leave the username and access token/password fields empty.
    - Select that you want the repository to be **Private**.
    - Type in the **repository name**. This can be anything you want.

      ![Import repository page](https://github.com/user-attachments/assets/2033bae4-1372-4e1d-81bc-3aa92236fa68)

    - Wait for the import to finish. This may take a couple minutes.
      Once ready, you will receive an email and, if you kept the window open, you'll see the following:

      ![Import finished screen](https://github.com/user-attachments/assets/5a69aee0-8664-47b5-ac0f-96d932b7b795)

      From here, you can click on the link to go to the repository's main page.

2. Generate fine-grained personal access token for imported repository with **Read and write** access to **Contents**, **Secrets**, and **Workflows**.

    - Go to https://github.com/settings/personal-access-tokens/new
    - Type in **Token name**.
    - Select **No expiration** as **Expiration**.

      ![Creating PAT - part 1](https://github.com/user-attachments/assets/8ef17dac-100d-4eda-90ef-47c3cc6ea193)

    - In **Repository access** section, select your repository.

      ![Creating PAT - part 2](https://github.com/user-attachments/assets/f19ad662-9d0a-4151-8e6f-77743e0c66a9)

    - Expand **Repository permissions** section.
    - Select **Access: Read and write** for **Contents**.

      ![Creating PAT - part 3](https://github.com/user-attachments/assets/5991c2ba-b623-499f-b23d-d90ffe4a0c91)
      ![Creating PAT - part 4](https://github.com/user-attachments/assets/f47c0019-ccdd-4151-bbf0-36809521c5c8)

    - Select **Access: Read and write** for **Secrets**.
    - Select **Access: Read and write** for **Workflows**.

      ![Creating PAT - part 5](https://github.com/user-attachments/assets/5304741d-6c0a-4a4b-bb47-828a8f80cabb)

    - Click on **Generate token**.

      ![Creating PAT - part 6](https://github.com/user-attachments/assets/d1ab13ee-b9e2-4c6f-935e-e025d9fc29ac)

    - Copy the generated token. **DO NOT** send it to anyone.

      ![Creating PAT - part 6](https://github.com/user-attachments/assets/b1a2786a-0a8e-498e-aee0-46016c6ab9bf)

3. Set GitHub Actions secret named `UPDATE_TOKEN` with the previously generated token as its value.

    - Go to **Settings** tab on your repository's page.
    - Expand  **Secrets and variables** and click on **Actions** on the left.
    - Click on **New repository secret**.

      !["Secrets and variables - Actions" page](https://github.com/user-attachments/assets/85c38472-dc2c-4a6f-9337-c5828e6bcf85)

    - Type in the following under **Name**:
      ```
      UPDATE_TOKEN
      ```
    - Paste the generated token in the **Secret** field.
    - Click **Add secret**.

      !["Secrets and variables - Actions" page](https://github.com/user-attachments/assets/ecf150fb-2000-4ffc-9e66-d4ba610c5bef)

4. Add keystore to GH Actions secrets.

    If you have a keystore already, you can follow the [How to manually add keystore to GH Actions secrets?](#how-to-manually-add-keystore-to-gh-actions-secrets) section instead.

    Otherwise, you can generate a fresh keystore and automatically set appropriate GH Actions secrets by running the **ci_utils_generate_secrets.yaml** workflow:

    - On repository's page, go to **Actions** tab.
    - Select **CI Utils - Generate required secrets** on the left.
    - Open the **Run workflow** popup.
    - Click on the green **Run workflow** button.

      ![Run workflow popup on "CI Utils - Generate required secrets" page](https://github.com/user-attachments/assets/156879cc-323c-41b5-9ad4-e9beb5ff1374)

    - After a couple seconds, a new *In progress* workflow run should appear and, eventually, it should finish:

      ![In progress workflow](https://github.com/user-attachments/assets/756123d6-4b1d-49b4-97a1-5d5ce052c16a)
      ![Finished workflow](https://github.com/user-attachments/assets/18789638-a02f-4257-a189-9adc6e44f499)

### Usage

To build AndroidAPS APK files, you need to use the **Build full Android APK** workflow:

1. Go to **Actions** tab.
2. Select **Build full Android APK** on the left.
3. Open the **Run workflow** popup.
4. Click on the green **Run workflow** button.

   ![Run workflow popup on "Build full Android APK" page](https://github.com/user-attachments/assets/355bcd83-963d-4715-a1ac-3619e6d082ca)

5. After a couple seconds, a new *In progress* workflow run should appear and, eventually (25-30 minutes), it should finish:

   ![In progress workflow](https://github.com/user-attachments/assets/ca5f49c0-2f45-4ea3-94e8-f5907a6d4942)
   ![Finished workflow]()

6. When the workflow finishes, you should receive an email notification about a new release on the repository.
   You can visit the release page to download the full APK file for mobile and wearOS app.

   ![Email notification about the release](https://github.com/user-attachments/assets/669db12c-6e3e-4ccb-b53b-01c059876deb)
   ![Main repository page with the Releases highlighted](https://github.com/user-attachments/assets/4e30cf82-d519-4978-97ab-51a267f76c50)
   ![Release page](https://github.com/user-attachments/assets/98d14ed4-d49d-4c9e-a703-ecc5747e5714)

The default branch (used when the option named "default" is selected as the **Branch to build from** in the **Run workflow** popup) to build from is the `master` branch but it can be changed by setting the `DEFAULT_BUILD_BRANCH` GitHub Actions variable to the name of that branch.

> [!NOTE]
> The **Pull AndroidAPS from upstream** workflow runs once a day to pull changes from AndroidAPS upstream repository
and triggers a build, if any changes are found.

## Updating

In rare scenarios, the workflows may need some changes. In such case, it's likely that my repository has been updated
with a newer workflow.

To update workflows, you need to use the **CI Utils - Update workflows to latest version** workflow:

1. Go to **Actions** tab.
2. Select **CI Utils - Update workflows to latest version** on the left.
3. Open the **Run workflow** popup.
4. Click on the green **Run workflow** button.

   ![Run workflow popup on "CI Utils - Update workflows to latest version" page](https://github.com/user-attachments/assets/29218887-1450-4d1d-a823-f8d627d82239)

5. After a couple seconds, a new *In progress* workflow run should appear and, eventually, it should finish:

   ![In progress workflow](https://github.com/user-attachments/assets/1db915f5-fb7f-46e2-ba5c-7a7c74084d05)
   ![Finished workflow](https://github.com/user-attachments/assets/54cac97a-07f3-4b10-92bd-6858da1ccc98)

## How to manually add keystore to GH Actions secrets?

If you have a keystore already from previous AndroidAPS builds, you can add it to GH Actions
so that you can update the app without having to uninstall your old build first.

The necessary secrets are:
- `KEYSTORE_BASE64` - base64-encoded contents of the keystore file with the key used for signing APKs.
- `KEY_ALIAS` - key's alias in the keystore
- `KEY_PASSWORD` - password used to decrypt the key stored in the keystore
- `STORE_PASSWORD` - password used to decrypt the keystore
