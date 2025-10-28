{
  description = "Critter Parade (LibGDX) - dev shell and runnable apps";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";
    systems.url = "github:nix-systems/default";
    nixgl = {
      url = "github:guibou/nixGL";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, systems, nixgl }:
    let
      forAllSystems = nixpkgs.lib.genAttrs (import systems);
    in
    {
      devShells = forAllSystems (system:
        let
          pkgs = import nixpkgs { inherit system; };
          runtimeLibs = with pkgs; [
            xorg.libX11
            xorg.libXext
            xorg.libXrandr
            xorg.libXinerama
            xorg.libXcursor
            xorg.libXxf86vm
            xorg.libXi
            libglvnd
            wayland
            libxkbcommon
            alsa-lib
            fontconfig
            freetype
          ];
          libPath = pkgs.lib.makeLibraryPath runtimeLibs;
          jdk = pkgs.jdk21;
          mvn = pkgs.maven;
          nb = pkgs.netbeans;
        in {
          default = pkgs.mkShell {
            packages = [ mvn jdk nb ] ++ runtimeLibs;
            shellHook = ''
              export JAVA_HOME=${jdk}
              export LD_LIBRARY_PATH=${libPath}:$LD_LIBRARY_PATH
              export MAVEN_OPTS="-Djava.library.path=${libPath}"
            '';
          };
        }
      );

      apps = forAllSystems (system:
        let
          pkgs = import nixpkgs { inherit system; };
          runtimeLibs = with pkgs; [
            xorg.libX11
            xorg.libXext
            xorg.libXrandr
            xorg.libXinerama
            xorg.libXcursor
            xorg.libXxf86vm
            xorg.libXi
            libglvnd
            wayland
            libxkbcommon
            alsa-lib
            fontconfig
            freetype
          ];
          libPath = pkgs.lib.makeLibraryPath runtimeLibs;
          jdk = pkgs.jdk21;
          mvn = pkgs.maven;
          runScript = pkgs.writeShellScriptBin "critter-parade-run" ''
            export JAVA_HOME=${jdk}
            export LD_LIBRARY_PATH=${libPath}:$LD_LIBRARY_PATH
            export MAVEN_OPTS="-Djava.library.path=${libPath}"
            exec ${mvn}/bin/mvn -q -f "$PWD/pom.xml" exec:java
          '';
          runScriptNixGL = pkgs.writeShellScriptBin "critter-parade-run-nixgl" ''
            export JAVA_HOME=${jdk}
            export LD_LIBRARY_PATH=${libPath}:$LD_LIBRARY_PATH
            export MAVEN_OPTS="-Djava.library.path=${libPath}"
            exec ${nixgl.packages.${system}.nixGLDefault}/bin/nixGL ${mvn}/bin/mvn -q -f "$PWD/pom.xml" exec:java
          '';
        in {
          default = {
            type = "app";
            program = "${runScript}/bin/critter-parade-run";
          };
          run = {
            type = "app";
            program = "${runScript}/bin/critter-parade-run";
          };
          run-nixgl = {
            type = "app";
            program = "${runScriptNixGL}/bin/critter-parade-run-nixgl";
          };
        }
      );
    };
}


