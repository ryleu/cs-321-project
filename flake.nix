{
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
    systems.url = "github:nix-systems/default";
  };

  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } {
      systems = import inputs.systems;
      perSystem =
        {
          config,
          self',
          pkgs,
          lib,
          system,
          ...
        }:
        let
          libs = with pkgs; [
            # GL
            libGL
            glfw
            glfw-wayland-minecraft
            mesa
            xorg.libX11
            xorg.libXrandr
            xorg.libXcursor
            xorg.libXi
            xorg.libXinerama
            xorg.libXxf86vm
            xorg.libxcb
            glfw-wayland

            # audio
            libpulseaudio
            openal

            flite

            jetbrains.jdk

            jetbrains.idea-community-bin
          ];
        in
        {
          devShells.default = pkgs.mkShell {
            nativeBuildInputs = with pkgs; [
              jetbrains.jdk
            ];

            buildInputs = libs;
            LD_LIBRARY_PATH = lib.makeLibraryPath libs;

            env = {
              JAVA_HOME = "${pkgs.jetbrains.jdk}/lib/openjdk/";
            };
          };
        };
    };
}
